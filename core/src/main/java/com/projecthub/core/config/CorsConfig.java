package com.projecthub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;

/**
 * Cross-Origin Resource Sharing (CORS) configuration.
 * Configures allowed origins, methods, headers, and other CORS-related settings
 * for the application's REST API endpoints.
 *
 * @since 1.0.0
 */
@Configuration
public class CorsConfig 
{

    /**
     * Array of allowed origins for CORS requests.
     * Defaults to http://localhost:4200 if not specified in properties.
     */
    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String[] allowedOrigins;

    public CorsConfig(String[] allowedOrigins) 
    {
        this.allowedOrigins = allowedOrigins;
    }

    /**
     * Configures CORS settings for the application.
     * Applies to all API endpoints under /api/**.
     *
     * @return WebMvcConfigurer with CORS configuration
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() 
    {
        return new WebMvcConfigurer() 
        {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry)
            {
                registry.addMapping("/api/**")
                        .allowedOrigins(allowedOrigins)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Origin", "Content-Type", "Accept", "Authorization")
                        .exposedHeaders("Authorization")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}