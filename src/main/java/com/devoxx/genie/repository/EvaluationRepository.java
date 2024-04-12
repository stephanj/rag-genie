package com.devoxx.genie.repository;

import com.devoxx.genie.domain.Evaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    @Query("SELECT e FROM Evaluation e WHERE e.id in :ids")
    List<Evaluation> findAllById(@Param("ids") List<Long> ids);

    Page<Evaluation> findAllByUserId(Long userId, Pageable pageable);

    Optional<Evaluation> findByIdAndUserId(Long id, Long userId);
}
