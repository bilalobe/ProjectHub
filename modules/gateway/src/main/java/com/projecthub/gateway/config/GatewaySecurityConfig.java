package com.projecthub.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authorization.HttpStatusServerAccessDeniedHandler;
import com.projecthub.auth.config.CoreSecurityConfig;
import org.springframework.context.annotation.Import;

/**
 * Gateway-specific security configuration that handles routing and API security.
 */
@Configuration
@EnableWebFluxSecurity
@Import(CoreSecurityConfig.class)
public class GatewaySecurityConfig {

    private final FortressServerAuthorizationManager authorizationManager;

    public GatewaySecurityConfig(FortressServerAuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                // Public endpoints
                .pathMatchers("/api/auth/**", "/actuator/health").permitAll()
                .pathMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                // Protected endpoints require authentication and authorization
                .anyExchange().access(authorizationManager)
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> {})
                .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedHandler(new HttpStatusServerAccessDeniedHandler(HttpStatus.FORBIDDEN))
            )
            .build();
    }
}