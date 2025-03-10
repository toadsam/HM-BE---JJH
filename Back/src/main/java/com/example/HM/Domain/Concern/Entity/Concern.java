package com.example.HM.Domain.Concern.Entity;

import com.example.HM.Domain.Solution.Entity.Solution;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Concern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID

    private String title; // 고민 제목

    @Lob
    private String description; // 고민 내용

    private LocalDateTime createdAt; // 생성일

    private LocalDateTime deadline; // 마감일

    @Enumerated(EnumType.STRING)
    private Status status; // 상태 ( pending || solved || ai_solved )

    @OneToOne(mappedBy = "concern", cascade = CascadeType.ALL)
    private Solution solution;

    public enum Status {
        PENDING,
        SOLVED,
        AI_SOLVED
    }

}
