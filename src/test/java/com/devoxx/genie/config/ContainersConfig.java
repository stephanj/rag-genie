package com.devoxx.genie.config;

import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {
    @Bean
    @ServiceConnection
    @RestartScope
    protected PostgreSQLContainer<?> postgreSQLContainer() {
        var dockerImage = DockerImageName.parse("pgvector/pgvector:pg16")
            .asCompatibleSubstituteFor("postgres");

        var postgresContainer = new PostgreSQLContainer<>(dockerImage).withInitScript("init.sql");
        postgresContainer.start();

        System.setProperty("genie.database.port", postgresContainer.getMappedPort(5432).toString());
        System.setProperty("genie.database.username", postgresContainer.getUsername());
        System.setProperty("genie.database.password", postgresContainer.getPassword());

        return postgresContainer;
    }
}
