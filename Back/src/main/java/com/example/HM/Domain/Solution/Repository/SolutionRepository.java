package com.example.HM.Domain.Solution.Repository;

import com.example.HM.Domain.Solution.Entity.Solution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolutionRepository extends JpaRepository<Solution, Long> {
}
