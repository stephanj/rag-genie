package com.devoxx.genie.repository;

import com.devoxx.genie.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the Comment entity.
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
