package com.projecthub.gateway.security;

import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.GlobalErrIds;
import org.apache.directory.fortress.core.model.Permission;
import org.apache.directory.fortress.core.model.Session;
import org.apache.directory.fortress.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Reactive authorization manager that integrates with Apache Fortress for RBAC-based
 * authorization decisions.
 * 
 * <p>This class serves as the Policy Enforcement Point (PEP) in the API Gateway and
 * delegates authorization decisions to Fortress Policy Decision Point (PDP).</p>
 */
@Component
public class FortressServerAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private static final Logger log = LoggerFactory.getLogger(FortressServerAuthorizationManager.class);
    private static final PathMatcher pathMatcher = new AntPathMatcher();
    
    // Map of API paths to Fortress object/operation pairs
    private static final Map<String, ApiPermissionMapping> API_PERMISSIONS = new HashMap<>();
    
    static {
        // User API permission mappings
        API_PERMISSIONS.put("/api/users", new ApiPermissionMapping("USER", "SEARCH"));
        API_PERMISSIONS.put("/api/users/*", new ApiPermissionMapping("USER", "READ"));
        API_PERMISSIONS.put("/api/users/*/profile", new ApiPermissionMapping("USER_PROFILE", "READ"));
        
        // Project API permission mappings
        API_PERMISSIONS.put("/api/projects", new ApiPermissionMapping("PROJECT", "SEARCH"));
        API_PERMISSIONS.put("/api/projects/*", new ApiPermissionMapping("PROJECT", "READ"));
        API_PERMISSIONS.put("/api/projects/*/tasks", new ApiPermissionMapping("PROJECT_TASK", "SEARCH"));
        
        // Submission API permission mappings
        API_PERMISSIONS.put("/api/submissions", new ApiPermissionMapping("SUBMISSION", "SEARCH"));
        API_PERMISSIONS.put("/api/submissions/*", new ApiPermissionMapping("SUBMISSION", "READ"));
        API_PERMISSIONS.put("/api/submissions/*/grades", new ApiPermissionMapping("SUBMISSION_GRADE", "READ"));
        
        // School API permission mappings
        API_PERMISSIONS.put("/api/schools", new ApiPermissionMapping("SCHOOL", "SEARCH"));
        API_PERMISSIONS.put("/api/schools/*", new ApiPermissionMapping("SCHOOL", "READ"));
        
        // Cohort API permission mappings
        API_PERMISSIONS.put("/api/cohorts", new ApiPermissionMapping("COHORT", "SEARCH"));
        API_PERMISSIONS.put("/api/cohorts/*", new ApiPermissionMapping("COHORT", "READ"));
        API_PERMISSIONS.put("/api/cohorts/*/students", new ApiPermissionMapping("COHORT_STUDENT", "SEARCH"));
        
        // Team API permission mappings
        API_PERMISSIONS.put("/api/teams", new ApiPermissionMapping("TEAM", "SEARCH"));
        API_PERMISSIONS.put("/api/teams/*", new ApiPermissionMapping("TEAM", "READ"));
        API_PERMISSIONS.put("/api/teams/*/members", new ApiPermissionMapping("TEAM_MEMBER", "SEARCH"));
        
        // Component API permission mappings
        API_PERMISSIONS.put("/api/components", new ApiPermissionMapping("COMPONENT", "SEARCH"));
        API_PERMISSIONS.put("/api/components/*", new ApiPermissionMapping("COMPONENT", "READ"));
    }

    private final AccessMgr accessManager;

    public FortressServerAuthorizationManager(AccessMgr accessManager) {
        this.accessManager = accessManager;
    }

    /**
     * Authorizes a request based on Fortress RBAC policies.
     * 
     * @param authentication The user's authentication token
     * @param context The authorization context containing the request
     * @return A Mono with the authorization decision (granted/denied)
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        ServerHttpRequest request = context.getExchange().getRequest();
        String requestPath = request.getPath().value();
        HttpMethod method = request.getMethod();
        
        // Find the permission mapping for this API path
        ApiPermissionMapping mapping = findPermissionMapping(requestPath);
        if (mapping == null) {
            log.debug("No permission mapping found for path {}", requestPath);
            // If no mapping exists, deny access by default (fail-safe)
            return Mono.just(new AuthorizationDecision(false));
        }
        
        // Convert HTTP method to operation
        String operation = convertHttpMethodToOperation(method, mapping.getDefaultOperation());
        
        // Create Fortress permission
        Permission permission = new Permission(mapping.getObjectType(), operation);
        
        return authentication
                .filter(Authentication::isAuthenticated)
                .cast(JwtAuthenticationToken.class)
                .flatMap(token -> {
                    try {
                        String username = token.getName();
                        String userId = getUserIdFromToken(token);
                        
                        // Create Fortress user and session
                        User fortressUser = new User(username);
                        if (userId != null) {
                            fortressUser.setUserId(userId);
                        }
                        
                        Session session = new Session(fortressUser);
                        
                        // Check permission using Fortress
                        boolean isGranted = checkPermission(session, permission);
                        
                        if (isGranted) {
                            log.debug("Access granted for user {} to {}:{}", username, 
                                    permission.getObjName(), permission.getOpName());
                        } else {
                            log.debug("Access denied for user {} to {}:{}", username, 
                                    permission.getObjName(), permission.getOpName());
                        }
                        
                        return Mono.just(new AuthorizationDecision(isGranted));
                    } catch (Exception e) {
                        log.error("Error checking permission", e);
                        // Fail closed - deny access on error
                        return Mono.just(new AuthorizationDecision(false));
                    }
                })
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

    /**
     * Finds the permission mapping for the given API path.
     * 
     * @param requestPath The API request path
     * @return The matching permission mapping or null if none found
     */
    private ApiPermissionMapping findPermissionMapping(String requestPath) {
        return API_PERMISSIONS.entrySet().stream()
                .filter(entry -> pathMatcher.match(entry.getKey(), requestPath))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Converts an HTTP method to a Fortress operation.
     * 
     * @param method The HTTP method
     * @param defaultOperation The default operation to use if method is not mapped
     * @return The Fortress operation string
     */
    private String convertHttpMethodToOperation(HttpMethod method, String defaultOperation) {
        if (method == null) {
            return defaultOperation;
        }
        
        return switch (method) {
            case GET -> "READ";
            case POST -> "CREATE";
            case PUT -> "UPDATE";
            case PATCH -> "UPDATE";
            case DELETE -> "DELETE";
            default -> defaultOperation;
        };
    }

    /**
     * Extracts the user ID from the JWT authentication token.
     * 
     * @param token The JWT authentication token
     * @return The user ID or null if not present
     */
    private String getUserIdFromToken(JwtAuthenticationToken token) {
        Object userId = token.getToken().getClaims().get("userId");
        if (userId != null) {
            try {
                // Try to validate as UUID to prevent injection
                UUID.fromString(userId.toString());
                return userId.toString();
            } catch (IllegalArgumentException e) {
                log.warn("Invalid userId format in token: {}", userId);
            }
        }
        return null;
    }

    /**
     * Checks if the given session has the requested permission.
     * 
     * @param session The user's Fortress session
     * @param permission The permission to check
     * @return true if permission is granted, false otherwise
     */
    private boolean checkPermission(Session session, Permission permission) {
        try {
            // Call Fortress access manager to check the permission
            accessManager.checkAccess(session, permission);
            return true;
        } catch (org.apache.directory.fortress.core.SecurityException e) {
            // Permission denied is an expected condition, not an error
            if (e.getErrorId() == GlobalErrIds.NOT_AUTHORIZED) {
                return false;
            }
            // Log unexpected errors
            log.error("Fortress security exception during permission check", e);
            return false;
        } catch (Exception e) {
            // Log other unexpected errors
            log.error("Unexpected error during permission check", e);
            return false;
        }
    }

    /**
     * Inner class representing a mapping between an API path and a Fortress permission.
     */
    private static class ApiPermissionMapping {
        private final String objectType;
        private final String defaultOperation;

        public ApiPermissionMapping(String objectType, String defaultOperation) {
            this.objectType = objectType;
            this.defaultOperation = defaultOperation;
        }

        public String getObjectType() {
            return objectType;
        }

        public String getDefaultOperation() {
            return defaultOperation;
        }
    }
}