package com.devoxx.genie.repository;

import com.devoxx.genie.domain.EvaluationResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationResultRepository extends JpaRepository<EvaluationResult, Long> {

    Page<EvaluationResult> findAllByUserId(Pageable pageable, Long userId);

    Optional<EvaluationResult> findByIdAndUserId(Long id, Long userId);
}
