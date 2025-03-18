package com.projecthub.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * Configuration for Jackson ObjectMapper.
 * Ensures proper serialization/deserialization of events.
 */
@Configuration
class ObjectMapperConfig {

    /**
     * Configure the ObjectMapper with appropriate modules and settings.
     */
    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            // Support for Java time types (Instant, LocalDateTime, etc.)
            registerModule(JavaTimeModule())
            // Proper Kotlin support (nullability, data classes, etc.)
            registerKotlinModule()
            // Configure serialization features
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }
}
