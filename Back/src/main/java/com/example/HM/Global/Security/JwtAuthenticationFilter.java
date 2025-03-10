package com.example.HM.Global.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // ✅ Spring Bean 등록
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 검증을 위한 Provider

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1️⃣ 요청에서 JWT 토큰 추출
        String token = jwtTokenProvider.resolveToken(request);

        // 2️⃣ 토큰이 존재하고 유효한 경우
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // Spring Security의 인증 컨텍스트에 사용자 정보 저장
            jwtTokenProvider.setAuthentication(token);
        }

        // 3️⃣ 필터 체인을 계속 진행
        filterChain.doFilter(request, response);
    }
}
