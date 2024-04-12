package com.devoxx.genie.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.mock;

/**
 * Test configuration for services whe using @SpringBootTest with webEnvironment = NONE.
 */
@TestConfiguration
public class ServicesTestConfig {
    @Bean // Required by UserService
    PasswordEncoder passwordEncoder() {
        return mock(PasswordEncoder.class);
    }

    @Bean // Required by UserJWTController
    AuthenticationManager authenticationManager() {
        return mock(AuthenticationManager.class);
    }
}
