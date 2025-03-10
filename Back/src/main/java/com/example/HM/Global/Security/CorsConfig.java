package com.example.HM.Global.Security; // 패키지 이름 (SecurityConfig.java와 동일한 패키지에 위치)

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 🔥 모든 도메인에서 접근 가능 (필요시 특정 도메인만 허용 가능)
        config.setAllowedOrigins(List.of("*")); // 예: List.of("https://example.com")

        // 🔥 모든 HTTP 메서드 허용 (GET, POST, PUT, DELETE, OPTIONS 등)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 🔥 모든 헤더 허용
        config.setAllowedHeaders(List.of("*"));

        // 🔥 클라이언트가 쿠키를 함께 보낼 수 있도록 허용
        config.setAllowCredentials(true);

        // 🔥 CORS 설정 적용
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
