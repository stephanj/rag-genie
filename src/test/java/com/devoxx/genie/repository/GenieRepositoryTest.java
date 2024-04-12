package com.devoxx.genie.repository;

import com.devoxx.genie.config.CacheTestConfig;
import com.devoxx.genie.config.ContainersConfig;
import com.devoxx.genie.config.DataJpaTestConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Testcontainers
@ActiveProfiles("test")
@DataJpaTest
@Import({
    ContainersConfig.class,
    CacheTestConfig.class,
    DataJpaTestConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public @interface GenieRepositoryTest {
}
