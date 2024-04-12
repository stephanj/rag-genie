package com.devoxx.genie.repository;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.domain.UserDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@GenieRepositoryTest
class UserRepositoryIT {

    @Autowired
    UserRepository userRepository;

    @Test
    void createUser() {
        User user = UserDataFactory.create();
        userRepository.saveAndFlush(user);
        assertThat(user.getId()).isNotNull();
    }

}
