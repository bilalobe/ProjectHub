package com.projecthub.auth.api;

import com.projecthub.auth.api.dto.PermissionDTO;
import reactor.core.publisher.Mono;

/**
 * Core authorization interface that defines the contract for authorization operations.
 * This interface abstracts Fortress RBAC functionality for use across modules.
 */
public interface AuthorizationFacade {
    /**
     * Check if a user has permission to perform an operation.
     *
     * @param username The username to check
     * @param permission The permission to verify
     * @return Mono<Boolean> indicating if permission is granted
     */
    Mono<Boolean> hasPermission(String username, PermissionDTO permission);
    
    /**
     * Check if a user has a specific role.
     *
     * @param username The username to check
     * @param role The role name to verify
     * @return Mono<Boolean> indicating if user has the role
     */
    Mono<Boolean> hasRole(String username, String role);
    
    /**
     * Get all permissions for a user.
     *
     * @param username The username to get permissions for
     * @return Mono<Set<PermissionDTO>> containing all user permissions
     */
    Mono<java.util.Set<PermissionDTO>> getUserPermissions(String username);
}