package com.projecthub.base.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cross-Origin Resource Sharing (CORS) configuration.
 * Configures allowed origins, methods, headers, and other CORS-related settings
 * for the application's REST API endpoints.
 *
 * @since 1.0.0
 */
@Configuration
public class CorsConfig {

    /**
     * Array of allowed origins for CORS requests.
     * Defaults to <a href="http://localhost:4200">...</a> if not specified in properties.
     */
    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String[] allowedOrigins;

    public CorsConfig() {
    }

    /**
     * Configures CORS settings for the application.
     * Applies to all API endpoints under /api/**.
     *
     * @return WebMvcConfigurer with CORS configuration
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull final CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins(CorsConfig.this.allowedOrigins)
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("Origin", "Content-Type", "Accept", "Authorization")
                    .exposedHeaders("Authorization")
                    .allowCredentials(true)
                    .maxAge(3600L);
            }
        };
    }
}
