package com.example.HM.Domain.AI.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AIConfig {

    @Value("${openai.api.key}")
    private String openAiKey;

    //Http 클라이언트
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openAiKey);
            return execution.execute(request, body);
        });
        return restTemplate;
    }

    // Http 헤더 설정
    @Bean
    public HttpHeaders httpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(openAiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
