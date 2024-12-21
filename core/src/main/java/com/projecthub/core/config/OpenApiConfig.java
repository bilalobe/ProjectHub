package com.projecthub.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI 3.0 documentation.
 * Sets up Swagger UI and API documentation endpoints with security schemes.
 *
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates and configures the OpenAPI documentation bean.
     * Includes security schemes, API information, and contact details.
     *
     * @return Configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(new Info()
                        .title("ProjectHub API")
                        .description("API documentation for ProjectHub application.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ProjectHub Team")
                                .email("support@projecthub.com")));
    }
}