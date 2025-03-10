package com.example.HM.Domain.Concern.Repository;

import com.example.HM.Domain.Concern.Entity.Concern;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcernRepository extends JpaRepository<Concern, Long> {
}
