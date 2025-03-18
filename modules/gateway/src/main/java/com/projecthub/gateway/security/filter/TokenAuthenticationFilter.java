package com.projecthub.gateway.security.filter;

import com.projecthub.auth.service.TokenValidationService;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Gateway filter that authenticates requests based on JWT tokens.
 * Extracts and validates the token in Authorization header.
 */
@Component
@Order(1)
public class TokenAuthenticationFilter implements WebFilter {

    private final TokenValidationService tokenValidationService;

    public TokenAuthenticationFilter(TokenValidationService tokenValidationService) {
        this.tokenValidationService = tokenValidationService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (tokenValidationService.isValid(token)) {
                // Add user ID and roles as headers to propagate downstream
                tokenValidationService.extractSubject(token).ifPresent(userId -> {
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("X-User-ID", userId)
                        .build();
                    exchange = exchange.mutate().request(mutatedRequest).build();
                });
            }
        }
        
        return chain.filter(exchange);
    }
}