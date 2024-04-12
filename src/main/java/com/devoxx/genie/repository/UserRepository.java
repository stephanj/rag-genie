package com.devoxx.genie.repository;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.service.util.CacheNames;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Instant dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmailIgnoreCase(String email);

    Optional<User> findOneByLoginIgnoreCase(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesById(Long id);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = CacheNames.USERS_BY_LOGIN)
    Optional<User> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = CacheNames.USERS_BY_EMAIL)
    Optional<User> findOneWithAuthoritiesByEmail(String email);

    Page<User> findAllByLoginNot(Pageable pageable, String login);

    @Query("""
        select u from User u
        left join u.authorities authority
        where :authority LIKE authority.name
        """)
    Page<User> findUsersByAuthority(@Param("authority") String authority, Pageable pageable);

    @Query("""
        select user from User user
        where lower(user.fullName) like :query or
              lower(user.login) like :query
        """)
    Page<User> findByFullNameOrLogin(@Param("query") String query, Pageable pageable);
}
