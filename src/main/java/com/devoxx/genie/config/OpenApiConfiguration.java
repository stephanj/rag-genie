package com.devoxx.genie.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "REST API", version = "1.0",
    description = "The Devoxx Genie REST endpoints.",
    contact = @Contact(name = "Stephan Janssen")),
    security = {@SecurityRequirement(name = "bearerToken")}
)
public class OpenApiConfiguration {
}
