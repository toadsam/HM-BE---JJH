package com.example.HM.Domain.Concern.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateConcernRequestDto {
    private String title; // 고민 제목
    private String description; // 고민 내용
    private LocalDateTime deadline; // 마감일
}
