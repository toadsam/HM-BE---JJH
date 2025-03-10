package com.example.HM.Global.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// JWT 사용하지 않는 버전의 Swwagger Config
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("HalleMalle") // API의 제목
                .description("고민을 작성하고 해결하거나 AI의 도움을 받을 수 있는 웹 애플리케이션 API.") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }
}