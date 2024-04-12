package com.devoxx.genie.service.user;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get admin user
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Optional<User> getAdminUser() {
        return userRepository.findOneWithAuthoritiesByLogin("admin");
    }
}
