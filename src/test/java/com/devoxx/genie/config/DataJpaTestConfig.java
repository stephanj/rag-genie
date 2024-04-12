package com.devoxx.genie.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@TestConfiguration
@Import({DatabaseConfiguration.class})
public class DataJpaTestConfig {
    @Bean
    @Primary
    public AuditorAware<String> springSecurityAuditorAware() {
        return () -> Optional.of("Test auditor");
    }
}
