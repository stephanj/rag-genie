package com.devoxx.genie.util;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

public class EndpointAvailabilityCondition implements ExecutionCondition {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointAvailabilityCondition.class);

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        final var optional = findAnnotation(context.getElement(), SkipWhenEndpointUnavailable.class);
        if (optional.isPresent()) {
            final SkipWhenEndpointUnavailable annotation = optional.get();
            final String uri = annotation.uri();
            WebClient webClient = WebClient.builder().build();
            try {
                Mono<Void> response = webClient.head()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Void.class);
                response.block(); // Block and wait for the response
                LOGGER.info("Connection to {} is open.", uri);
                // If no exceptions are thrown, the URL is reachable
                return ConditionEvaluationResult.enabled("Connection is up");
            } catch (Exception e) {
                LOGGER.warn("Connection to {} could not be established.", uri, e);
                return ConditionEvaluationResult.disabled("Connection is down");
            }
        }
        return ConditionEvaluationResult.enabled("No assumptions, moving on...");
    }

}
