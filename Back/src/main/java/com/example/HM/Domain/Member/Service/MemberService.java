package com.example.HM.Domain.Member.Service;

import com.example.HM.Domain.Member.DTO.MemberDTO;
import com.example.HM.Domain.Member.Entity.MemberEntity;
import com.example.HM.Domain.Member.Repository.MemberRepository;
import com.example.HM.Global.Security.JwtTokenProvider;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtTokenProvider jwtTokenProvider; // ✅ JWT 토큰 프로바이더 추가

    // 이메일 인증번호 저장 (이메일 → 인증번호)
    private final Map<String, String> emailVerificationCodes = new HashMap<>();
    // 인증번호 유효시간 저장 (이메일 → 유효기간)
    private final Map<String, LocalDateTime> verificationCodeExpiry = new HashMap<>();
    // 인증된 이메일 저장 (이메일 → 인증 여부)
    private final Map<String, Boolean> verifiedEmails = new HashMap<>();

    /**
     * 🔹 이메일 인증번호 생성 & 전송 (포트 587 적용)
     */
    public void sendEmailVerificationCode(String email) {
        String code = generateVerificationCode();
        emailVerificationCodes.put(email, code);
        verificationCodeExpiry.put(email, LocalDateTime.now().plusMinutes(5)); // 5분 후 만료
        log.info("✅ 인증번호 생성 - 이메일: {} | 인증번호: {}", email, code);

        try {
            // JavaMailSenderImpl로 SMTP 설정
            JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) mailSender;
            mailSenderImpl.setPort(587);
            mailSenderImpl.getJavaMailProperties().put("mail.smtp.starttls.enable", "true");

            // 이메일 전송
            MimeMessage message = mailSenderImpl.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom("toadsam@naver.com");  // ✅ 반드시 추가!
            helper.setTo(email);
            helper.setSubject("회원가입 인증번호");
            helper.setText("회원가입을 위한 인증번호: " + code + "\n\n📌 인증번호는 5분 동안 유효합니다.");

            mailSenderImpl.send(message);
            log.info("📩 인증번호 이메일 전송 완료 - {}", email);
        } catch (MessagingException e) {
            log.error("❌ 이메일 전송 실패 (MessagingException): {}", e.getMessage());
            throw new RuntimeException("이메일 전송 실패 (MessagingException)", e);
        } catch (Exception e) {
            log.error("❌ 이메일 전송 실패 (Exception): {}", e.getMessage());
            throw new RuntimeException("이메일 전송 실패 (Exception)", e);
        }
    }

    /**
     * 🔹 이메일 인증번호 확인
     */
    public boolean verifyEmailCode(String email, String code) {
        if (!emailVerificationCodes.containsKey(email)) {
            return false;
        }

        if (verificationCodeExpiry.get(email).isBefore(LocalDateTime.now())) {
            emailVerificationCodes.remove(email);
            verificationCodeExpiry.remove(email);
            return false;
        }

        if (emailVerificationCodes.get(email).equals(code)) {
            verifiedEmails.put(email, true);
            emailVerificationCodes.remove(email);
            verificationCodeExpiry.remove(email);
            return true;
        }

        return false;
    }

    /**
     * 🔹 회원가입 (이메일 인증 필수)
     */
    public void save(MemberDTO memberDTO) {
        log.info("회원가입 시도: {}", memberDTO.getEmail());

        if (!verifiedEmails.getOrDefault(memberDTO.getEmail(), false)) {
            log.warn("이메일 인증되지 않음: {}", memberDTO.getEmail());
            throw new IllegalArgumentException("이메일 인증을 먼저 진행해주세요.");
        }

        Optional<MemberEntity> existingUser = memberRepository.findByEmail(memberDTO.getEmail());
        if (existingUser.isPresent()) {
            log.warn("이미 존재하는 이메일: {}", memberDTO.getEmail());
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO);
        memberEntity.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        memberRepository.save(memberEntity);

        verifiedEmails.remove(memberDTO.getEmail());
        log.info("회원가입 성공: {}", memberDTO.getEmail());
    }

    /**
     * 🔹 이메일 중복 확인
     */
    public boolean checkEmailDuplicate(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    /**
     * 🔹 로그인 처리 (JWT 토큰 발급)
     */
    public String login(String email, String password) {
        Optional<MemberEntity> user = memberRepository.findByEmail(email);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return jwtTokenProvider.createToken(email); // ✅ 토큰 문자열 반환
        }

        return null; // ✅ 로그인 실패 시 null 반환
    }

    /**
     * 🔹 인증번호 생성 (6자리)
     */
    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }


    /**
     * 🔹 비밀번호 찾기 (이메일 기반)
     * - 사용자가 이메일과 인증번호를 입력하면 기존 비밀번호를 복구하거나
     * - 보안상, 임시 비밀번호를 발급하여 이메일로 전송 가능
     */
    public String findPasswordByEmail(String email) {
        // 1️⃣ 데이터베이스에서 이메일로 회원 조회
        Optional<MemberEntity> optionalMember = memberRepository.findByEmail(email);

        if (optionalMember.isEmpty()) {
            log.warn("🚫 [ERROR] 존재하지 않는 이메일: {}", email);
            throw new IllegalArgumentException("등록되지 않은 이메일입니다.");
        }

        MemberEntity member = optionalMember.get();

        // 2️⃣ 보안상 기존 비밀번호를 반환하는 대신, 임시 비밀번호 발급 (권장)
        String tempPassword = generateTemporaryPassword();
        member.setPassword(passwordEncoder.encode(tempPassword)); // 암호화 후 저장
        memberRepository.save(member);

        // 3️⃣ 사용자의 이메일로 임시 비밀번호 전송 (권장)
        sendTemporaryPasswordByEmail(email, tempPassword);

        log.info("✅ [SUCCESS] 임시 비밀번호 발급 및 이메일 전송 완료: {}", email);
        return "📩 임시 비밀번호가 이메일로 전송되었습니다.";
    }

    /**
     * 🔹 임시 비밀번호 생성 (8자리 영문 + 숫자 조합)
     */
    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    /**
     * 🔹 임시 비밀번호를 이메일로 전송
     */
    private void sendTemporaryPasswordByEmail(String email, String tempPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom("toadsam@naver.com");
            helper.setTo(email);
            helper.setSubject("임시 비밀번호 안내");
            helper.setText("새로운 임시 비밀번호: " + tempPassword + "\n\n📌 로그인 후 반드시 비밀번호를 변경해주세요!");

            mailSender.send(message);
            log.info("📩 [EMAIL SENT] 임시 비밀번호 전송 완료 - {}", email);
        } catch (MessagingException e) {
            log.error("❌ 이메일 전송 실패 (MessagingException): {}", e.getMessage());
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

}
