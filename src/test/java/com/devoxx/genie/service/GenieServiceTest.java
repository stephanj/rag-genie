package com.devoxx.genie.service;

import com.devoxx.genie.config.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Transactional
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(
    classes = {
        ContainersConfig.class,
        CacheTestConfig.class,
        ServicesTestConfig.class
    },
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
public @interface GenieServiceTest {
}
