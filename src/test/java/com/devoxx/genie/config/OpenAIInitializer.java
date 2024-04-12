package com.devoxx.genie.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(initializers = OpenAIInitializer.class)
public class OpenAIInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAIInitializer.class);

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
        String openai_api_key;
        try {
            Dotenv dotenv = Dotenv.load();
            openai_api_key = dotenv.get("OPENAI_API_KEY");

            LOGGER.info("Using .env file to get OPENAI_API_KEY");
            assertThat(openai_api_key).as("OPENAI_API_KEY is not set in .env file").isNotNull();
        } catch (Exception e) {
            // The .env file is not present in CI/CD environment so get the OPENAI_API_KEY from environment variables
            LOGGER.info("Using environment variable to get OPENAI_API_KEY");

            // Get the GitLab CI/CD variable OPENAI_API_KEY
            openai_api_key = System.getenv("OPENAI_API_KEY");
        }
        TestPropertyValues.of("openai.api.key=" + openai_api_key).applyTo(applicationContext);
    }
}
