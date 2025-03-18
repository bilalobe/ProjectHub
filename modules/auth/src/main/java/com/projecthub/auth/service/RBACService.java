package com.projecthub.auth.service;

import java.util.List;
import java.util.Set;

/**
 * Service responsible for Role-Based Access Control operations.
 */
public interface RBACService {
    
    /**
     * Checks if a user has a specific permission.
     *
     * @param userId The user ID to check
     * @param permission The permission to verify
     * @return true if the user has the permission, false otherwise
     */
    boolean hasPermission(String userId, String permission);
    
    /**
     * Gets all permissions assigned to a user through their roles.
     *
     * @param userId The user ID to check
     * @return Set of permission strings
     */
    Set<String> getUserPermissions(String userId);
    
    /**
     * Gets all roles assigned to a user.
     *
     * @param userId The user ID to check
     * @return List of role names
     */
    List<String> getUserRoles(String userId);
    
    /**
     * Assigns a role to a user.
     *
     * @param userId The user ID to assign the role to
     * @param roleName The name of the role to assign
     */
    void assignRole(String userId, String roleName);
    
    /**
     * Removes a role from a user.
     *
     * @param userId The user ID to remove the role from
     * @param roleName The name of the role to remove
     */
    void removeRole(String userId, String roleName);
}