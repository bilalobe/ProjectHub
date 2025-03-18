package com.projecthub.gateway.security.filter;

import com.projecthub.auth.service.RBACService;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Gateway filter that enforces authorization rules after authentication.
 * Checks if the authenticated user has the required permissions for the requested resource.
 */
@Component
@Order(2) // Execute after authentication filter
public class AuthorizationFilter implements WebFilter {

    private final RBACService rbacService;

    public AuthorizationFilter(RBACService rbacService) {
        this.rbacService = rbacService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String userId = request.getHeaders().getFirst("X-User-ID");
        String path = request.getPath().value();
        String method = request.getMethod().name();

        // Skip authorization check if not authenticated
        if (userId == null) {
            return chain.filter(exchange);
        }

        // Determine required permission based on path and method
        String requiredPermission = mapToPermission(path, method);

        // If no specific permission mapping, continue
        if (requiredPermission == null) {
            return chain.filter(exchange);
        }

        // Check if user has the required permission
        if (rbacService.hasPermission(userId, requiredPermission)) {
            return chain.filter(exchange);
        } else {
            // Deny access if permission check fails
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * Maps request path and method to required permission string.
     * This is a simplified example - in production, would use a more sophisticated mapping system.
     *
     * @param path The request path
     * @param method The HTTP method
     * @return The required permission string, or null if no specific permission is required
     */
    private String mapToPermission(String path, String method) {
        // Project related endpoints
        if (path.matches("/api/v1/projects(/.*)?")) {
            if ("GET".equals(method)) {
                return "project:read";
            } else if ("POST".equals(method)) {
                return "project:create";
            } else if ("PUT".equals(method) || "PATCH".equals(method)) {
                return "project:update";
            } else if ("DELETE".equals(method)) {
                return "project:delete";
            }
        }
        
        // User management endpoints
        if (path.matches("/api/v1/users(/.*)?")) {
            if ("GET".equals(method)) {
                return "user:read";
            } else if ("POST".equals(method)) {
                return "user:create";
            } else if ("PUT".equals(method) || "PATCH".equals(method)) {
                return "user:update";
            } else if ("DELETE".equals(method)) {
                return "user:delete";
            }
        }
        
        // Admin endpoints
        if (path.matches("/api/v1/admin(/.*)?")) {
            return "admin:access";
        }
        
        // Default to no specific permission required
        return null;
    }
}