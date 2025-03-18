package com.projecthub.gateway.filter;

import org.apache.directory.fortress.core.model.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gateway filter factory that propagates Fortress permission information to downstream services.
 * 
 * <p>This filter enhances requests with security context information by adding custom headers
 * that contain the user's roles and permissions. This allows downstream services to make
 * authorization decisions without having to re-query Fortress.</p>
 */
@Component
public class FortressPermissionPropagationFilter extends AbstractGatewayFilterFactory<FortressPermissionPropagationFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(FortressPermissionPropagationFilter.class);
    
    private static final String X_USER_ID_HEADER = "X-User-Id";
    private static final String X_USER_ROLES_HEADER = "X-User-Roles";
    private static final String X_USER_PERMISSIONS_HEADER = "X-User-Permissions";
    private static final String ROLE_PREFIX = "ROLE_";

    public FortressPermissionPropagationFilter() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("enabled");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!config.isEnabled()) {
                return chain.filter(exchange);
            }

            return ReactiveSecurityContextHolder.getContext()
                    .filter(securityContext -> securityContext.getAuthentication() != null)
                    .flatMap(securityContext -> {
                        ServerHttpRequest mutatedRequest = enrichRequestWithSecurityContext(
                                exchange.getRequest(), securityContext);
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    })
                    .switchIfEmpty(chain.filter(exchange));
        };
    }

    /**
     * Enriches the request with security context information in the form of HTTP headers.
     * 
     * @param request The original request
     * @param securityContext The security context containing authentication details
     * @return A mutated request with additional security headers
     */
    private ServerHttpRequest enrichRequestWithSecurityContext(ServerHttpRequest request, SecurityContext securityContext) {
        Authentication authentication = securityContext.getAuthentication();
        String userId = authentication.getName();
        
        // Extract roles (prefixed with ROLE_)
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(ROLE_PREFIX))
                .map(authority -> authority.substring(ROLE_PREFIX.length()))
                .collect(Collectors.joining(","));
        
        // Extract permissions (not prefixed with ROLE_)
        String permissions = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> !authority.startsWith(ROLE_PREFIX))
                .collect(Collectors.joining(","));
        
        log.debug("Propagating security context for user {}: roles=[{}], permissions=[{}]", 
                userId, roles, permissions);
                
        return request.mutate()
                .header(X_USER_ID_HEADER, userId)
                .header(X_USER_ROLES_HEADER, roles)
                .header(X_USER_PERMISSIONS_HEADER, permissions)
                .build();
    }
    
    /**
     * Configuration class for the filter.
     */
    public static class Config {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}