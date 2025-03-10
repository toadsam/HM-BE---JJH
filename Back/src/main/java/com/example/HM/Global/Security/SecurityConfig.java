package com.example.HM.Global.Security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService; // ✅ Google OAuth2 사용자 서비스

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 🔥 CSRF 보호 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 🔥 세션 미사용 (JWT)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 🔥 CORS 관련 OPTIONS 요청 허용
                        .requestMatchers(
                                "/member/login",
                                "/member/save",
                                "/member/send-email", // ✅ 이메일 인증 API 허용
                                "/member/verify-code", // ✅ 인증번호 확인 API 허용
                                "/oauth2/**", // ✅ 소셜 로그인 URL 허용
                                "/login/oauth2/code/google", // ✅ 구글 로그인 리디렉션 허용
                                "/home",
                                "/api/user-info",
                                "/member/find-password",
                                "member/find",
                                "member/testhtml"
                        )
                        .permitAll()
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/member/login") // ✅ 로그인 페이지 URL 설정
                        .defaultSuccessUrl("/home", true) // ✅ 로그인 성공 시 이동할 URL
                        .failureUrl("/member/login?error") // ✅ 로그인 실패 시 이동할 URL
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // ✅ OAuth2 사용자 서비스 적용
                        )
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // ✅ JWT 필터 적용
                .addFilterAfter(jwtAuthenticationFilter, OAuth2LoginAuthenticationFilter.class); // ✅ OAuth2 로그인 후 JWT 적용

        return http.build();
    }
}
