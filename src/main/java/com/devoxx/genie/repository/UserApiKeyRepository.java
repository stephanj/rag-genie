package com.devoxx.genie.repository;

import com.devoxx.genie.domain.UserAPIKey;
import com.devoxx.genie.domain.enumeration.LanguageModelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserApiKeyRepository extends JpaRepository<UserAPIKey, Long> {

    @Query("""
            SELECT u FROM UserAPIKey u
            WHERE u.user.id = :userId AND u.id = :id
        """)
    Optional<UserAPIKey> findByUserIdAndId(@Param("userId") Long userId, @Param("id") Long id);

    List<UserAPIKey> findAllByUserId(Long userId);

    Optional<UserAPIKey> findByUserIdAndLanguageType(Long userId, LanguageModelType languageModelType);
}
