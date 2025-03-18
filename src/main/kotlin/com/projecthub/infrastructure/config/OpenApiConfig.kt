package com.projecthub.infrastructure.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.tags.Tag
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun projectHubApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("ProjectHub API")
                    .description("API documentation for ProjectHub milestone management and related features")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("ProjectHub Team")
                            .url("https://github.com/projecthub")
                    )
            )
            .tags(
                listOf(
                    Tag().name("Milestones").description("Milestone management operations"),
                    Tag().name("Milestone Events").description("Event handling for milestone state changes"),
                    Tag().name("Milestone Metrics").description("Performance metrics for milestone operations")
                )
            )
    }
}
