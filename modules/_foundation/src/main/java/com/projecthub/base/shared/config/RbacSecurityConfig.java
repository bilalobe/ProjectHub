package com.projecthub.base.shared.config;

import com.projecthub.base.shared.security.permission.Permission;
import com.projecthub.base.shared.security.service.OptimizedSecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.model.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchical.RoleHierarchy;
import org.springframework.security.access.hierarchical.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Base security configuration for all modules using Role-Based Access Control with Apache Fortress.
 * Provides role hierarchy and optimized permission checking.
 * <p>
 * Modules should extend this and define their own specific RBAC configurations.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class RbacSecurityConfig {

    private final AccessMgr accessManager;
    private final ReviewMgr reviewManager;
    private final OptimizedSecurityService securityService;

    /**
     * Creates a role hierarchy for Spring Security.
     * This establishes the inheritance pattern for roles.
     *
     * @return A configured RoleHierarchy instance
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        // Define common role hierarchy using spring standard format
        hierarchy.setHierarchy("""
            ROLE_ADMIN > ROLE_TEACHER
            ROLE_TEACHER > ROLE_STUDENT
            ROLE_STUDENT > ROLE_GUEST
            ROLE_GUEST > ROLE_ANONYMOUS
            """);
        return hierarchy;
    }

    /**
     * Creates a security expression handler that incorporates the role hierarchy.
     * This allows Spring Security expressions to respect role inheritance.
     *
     * @param roleHierarchy The role hierarchy to use
     * @return A configured MethodSecurityExpressionHandler
     */
    @Bean
    public MethodSecurityExpressionHandler expressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }

    /**
     * Checks if the current user session has the given permission using the optimized security service.
     * Uses caching and batching optimizations for better performance.
     *
     * @param session The user's Fortress session
     * @param resourceType The resource type (e.g., "school", "cohort", etc.)
     * @param operation The operation being performed (e.g., "create", "update", etc.)
     * @return true if the permission is granted, false otherwise
     */
    public boolean hasPermission(Session session, String resourceType, String operation) {
        Permission permission = createPermission(resourceType, operation, null);
        return securityService.hasPermission(session, permission);
    }

    /**
     * Checks if the current user session has the given permission for a specific resource.
     *
     * @param session The user's Fortress session
     * @param resourceType The resource type
     * @param operation The operation being performed
     * @param resourceId The specific resource identifier
     * @return true if the permission is granted, false otherwise
     */
    public boolean hasPermission(Session session, String resourceType, String operation, String resourceId) {
        Permission permission = createPermission(resourceType, operation, resourceId);
        return securityService.hasPermission(session, permission);
    }

    /**
     * Creates a Permission instance for checking
     */
    protected static Permission createPermission(String resourceType, String operation, String resourceId) {
        return new Permission() {
            @Override
            public String getResourceType() {
                return resourceType;
            }

            @Override
            public String getOperation() {
                return operation;
            }

            @Override
            public boolean requiresOwnership() {
                return false;
            }

            @Override
            public boolean isAdministrative() {
                return false;
            }

            @Override
            public String getResourceId() {
                return resourceId;
            }
        };
    }
}
