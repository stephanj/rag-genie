package com.devoxx.genie.repository;

import com.devoxx.genie.domain.LanguageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data repository for the Language Model entity.
 */
@Repository
public interface LanguageModelRepository extends JpaRepository<LanguageModel, Long> {

    Optional<LanguageModel> findByName(String name);
}
