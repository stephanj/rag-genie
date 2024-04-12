package com.devoxx.genie.repository;

import com.devoxx.genie.domain.Interaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {

    Page<Interaction> findAllByUserId(Pageable pageable, Long userId);

    Optional<Interaction> findByIdAndUserId(Long id, Long userId);
}
