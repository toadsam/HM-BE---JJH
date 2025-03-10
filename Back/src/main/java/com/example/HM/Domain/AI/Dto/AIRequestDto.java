package com.example.HM.Domain.AI.Dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AIRequestDto {
    private String model;
    private List<Message> messages;

    public AIRequestDto(String model, String prompt) {
        this.model = model;
        this.messages =  new ArrayList<>();
        this.messages.add(new Message("system", prompt));
    }
}
