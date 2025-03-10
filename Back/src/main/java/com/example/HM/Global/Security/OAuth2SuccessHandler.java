package com.example.HM.Global.Security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        // ✅ JWT 생성
        String token = jwtTokenProvider.createToken(email);

        // ✅ JWT를 포함하여 홈으로 리다이렉트
        String redirectUrl = UriComponentsBuilder.fromUriString("/home")
                .queryParam("token", token)
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }
}
