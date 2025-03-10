package com.example.HM.Domain.AI.Controller;

import com.example.HM.Domain.AI.Dto.AIRequestDto;
import com.example.HM.Domain.AI.Dto.AIResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/AI")
public class AIResponseController {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    @GetMapping("/Solving")
    public String chat(@RequestParam(name = "prompt")String prompt){
        AIRequestDto aiRequest = new AIRequestDto(model, prompt);
        AIResponseDto aiResponse =  template.postForObject(apiURL, aiRequest, AIResponseDto.class);
        return aiResponse.getChoices().get(0).getMessage().getContent();
    }
}
