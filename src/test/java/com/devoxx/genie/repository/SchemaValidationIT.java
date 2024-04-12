package com.devoxx.genie.repository;

import com.devoxx.genie.config.ContainersConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest(
    properties = "spring.jpa.hibernate.ddl-auto=validate"
)
@ActiveProfiles("test")
@Import({ContainersConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SchemaValidationIT {

    @Test
    @DisplayName("DB Schema is valid")
    void validateSchema() {
        assertDoesNotThrow(() -> {
            // This test is only to validate the schema
            // ref: https://vladmihalcea.com/validate-ddl-schema-spring-hibernate/
        });
    }

}
