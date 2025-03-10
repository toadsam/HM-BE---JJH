package com.example.HM.Global.Security;

import com.example.HM.Domain.Member.Entity.MemberEntity;
import com.example.HM.Domain.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // ✅ 이메일을 기준으로 회원 정보 조회
        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword()) // 🔥 비밀번호는 반드시 암호화해서 저장해야 함!
                .roles("USER") // 🔥 기본 역할 설정
                .build();
    }
}
