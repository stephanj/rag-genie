package com.devoxx.genie.repository;

import com.devoxx.genie.domain.EmbeddingModelReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the Embedding Model Reference entity.
 */
@Repository
public interface EmbeddingModelReferenceRepository extends JpaRepository<EmbeddingModelReference, Long> {

}
