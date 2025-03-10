package com.example.HM.Domain.Concern.Service;

import com.example.HM.Domain.AI.Controller.AIResponseController;
import com.example.HM.Domain.Concern.Entity.Concern;
import com.example.HM.Domain.Concern.Repository.ConcernRepository;
import com.example.HM.Domain.Solution.Entity.Solution;
import com.example.HM.Domain.Solution.Repository.SolutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcernService {

    private final ConcernRepository concernRepository;
    private final SolutionRepository solutionRepository;
    private final AIResponseController aiController;

    public List<Concern> getAllConcerns(){
        return concernRepository.findAll();
    }

    public Concern getConcernById(Long id) {
        return concernRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Concern not found with id: " + id));
    }

    @Transactional
    public Concern createConcern(String title, String description, LocalDateTime deadline) {
        Concern concern = Concern.builder()
                .title(title)
                .description(description)
                .deadline(deadline)
                .createdAt(LocalDateTime.now())
                .status(Concern.Status.PENDING)
                .build();

        return concernRepository.save(concern);
    }


    // 고민 해결 (1) : 직접 해결
    @Transactional
    public Concern resolveConcern(Long id, String solutionContent) {
        Concern concern = getConcernById(id);

        Solution solution = Solution.builder()
                .content(solutionContent)
                .createdAt(LocalDateTime.now())
                .concern(concern)
                .build();

        solutionRepository.save(solution);
        concern.setStatus(Concern.Status.SOLVED);
        concern.setSolution(solution);
        return concernRepository.save(concern);
    }

    // 고민 해결 (2) : ai 해결
    @Transactional
    public Concern resolveWithAI(Long id) {
        Concern concern = getConcernById(id);
        String aiSolution = aiController.chat(concern.getDescription());

        // AI 답변을 Solution에 저장
        Solution solution = Solution.builder()
                .content(aiSolution)
                .createdAt(LocalDateTime.now())
                .concern(concern)
                .build();

        solutionRepository.save(solution);
        concern.setStatus(Concern.Status.AI_SOLVED);
        concern.setSolution(solution);
        return concernRepository.save(concern);    }
}
