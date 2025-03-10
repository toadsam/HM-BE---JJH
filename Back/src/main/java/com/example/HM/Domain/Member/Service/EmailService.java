package com.example.HM.Domain.Member.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final Map<String, String> verificationCodes = new HashMap<>(); // 이메일 - 인증번호 저장

    @Value("${spring.mail.username}")  // 현재 사용 중인 이메일 가져오기
    private String senderEmail;

    // 이메일 전송 메서드
    public void sendVerificationEmail(String email) {
        String verificationCode = generateVerificationCode();
        verificationCodes.put(email, verificationCode); // 이메일과 인증번호 저장

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setFrom(senderEmail);
            helper.setSubject("회원가입 인증번호");
            helper.setText("인증번호: " + verificationCode);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    // 인증번호 검증 메서드
    public boolean verifyCode(String email, String code) {
        return verificationCodes.containsKey(email) && verificationCodes.get(email).equals(code);
    }

    // 6자리 랜덤 인증번호 생성
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // 000000~999999 사이 랜덤 숫자
    }
}
