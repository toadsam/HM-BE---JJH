package com.example.HM.Domain.Solution.Entity;

import com.example.HM.Domain.Concern.Entity.Concern;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "concern_id")
    private Concern concern;
}
