package com.projecthub.gateway.config;

import com.projecthub.auth.service.TokenValidationService;
import com.projecthub.gateway.security.filter.AuthorizationFilter;
import com.projecthub.gateway.security.filter.RateLimitingFilter;
import com.projecthub.gateway.security.filter.TokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for the gateway module.
 * Sets up web security, CORS policy, and security filters.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final TokenValidationService tokenValidationService;

    public SecurityConfig(TokenValidationService tokenValidationService) {
        this.tokenValidationService = tokenValidationService;
    }

    /**
     * Configures the security filter chain.
     * Public endpoints are permitted without authentication while others require authentication.
     * 
     * @param http The ServerHttpSecurity to configure
     * @return The configured SecurityWebFilterChain
     */
    @Bean
    @Order(1)
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authorizeExchange()
                .pathMatchers("/api/v1/auth/**", "/actuator/**", "/public/**").permitAll()
                // API documentation endpoints
                .pathMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                // Health check endpoints
                .pathMatchers("/health", "/info").permitAll()
                // Require authentication for all other endpoints
                .anyExchange().authenticated()
            .and().build();
    }

    /**
     * Configures CORS policy for the gateway.
     * Allows specified origins, methods, and headers.
     * 
     * @return The configured CorsWebFilter
     */
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Configure allowed origins (replace with actual origins in production)
        config.setAllowedOrigins(List.of("http://localhost:4200", "https://projecthub.example.com"));
        
        // Configure allowed HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Configure allowed headers
        config.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With", 
            "Accept", 
            "X-XSRF-TOKEN"
        ));
        
        config.setMaxAge(3600L);
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("X-Total-Count", "X-RateLimit-Limit", "X-RateLimit-Reset"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}