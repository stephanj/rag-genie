package com.devoxx.genie.repository;

import com.devoxx.genie.domain.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data repository for the Content entity.
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, Long>, JpaSpecificationExecutor<Content> {

    Page<Content> findAllByUserId(Pageable pageable, Long userId);

    @Query("""
        select c from Content c
        where c.name like CONCAT('%', :name, '%')
        """)
    Optional<Content> findByName(@Param("name") String name);
}
