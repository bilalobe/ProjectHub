package com.projecthub.infrastructure.config.documentation

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun projectHubOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("ProjectHub API")
                    .description("API for managing projects and workflows")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("ProjectHub Team")
                            .email("team@projecthub.com")
                    )
            )
    }
}
