package com.tinyledger.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Tiny Ledger API",
                version = "1.0.0",
                description = "In-memory ledger API for managing bank accounts",
                contact = @Contact(name = "Pedro Lopes", email = "pedrolopes095@gmail.com")
        )
)
public class OpenApiConfig {
}

