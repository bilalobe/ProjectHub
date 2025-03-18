package com.projecthub.domain.security.rbac

import com.projecthub.domain.security.permission.Permission
import com.projecthub.domain.user.UserId

/**
 * Service interface for Role-Based Access Control (RBAC).
 * This provides the core permission checking functionality used throughout the application.
 */
interface RBACService {

    /**
     * Checks if a user has the specified permission.
     *
     * @param userId The ID of the user to check
     * @param permission The permission to check for
     * @return true if the user has the permission, false otherwise
     */
    suspend fun hasPermission(userId: UserId, permission: Permission): Boolean

    /**
     * Checks if a user has the specified permission for a specific resource.
     *
     * @param userId The ID of the user to check
     * @param permission The permission to check for
     * @param resourceId The ID of the specific resource to check
     * @return true if the user has the permission for the resource, false otherwise
     */
    suspend fun hasPermissionForResource(
        userId: UserId,
        permission: Permission,
        resourceId: String
    ): Boolean

    /**
     * Assigns a role to a user.
     *
     * @param userId The ID of the user
     * @param roleId The ID of the role to assign
     */
    suspend fun assignRole(userId: UserId, roleId: String)

    /**
     * Removes a role from a user.
     *
     * @param userId The ID of the user
     * @param roleId The ID of the role to remove
     */
    suspend fun removeRole(userId: UserId, roleId: String)

    /**
     * Gets all roles assigned to a user.
     *
     * @param userId The ID of the user
     * @return A list of role IDs assigned to the user
     */
    suspend fun getUserRoles(userId: UserId): List<String>

    /**
     * Gets all permissions available to a user based on their assigned roles.
     *
     * @param userId The ID of the user
     * @return A set of permissions available to the user
     */
    suspend fun getUserPermissions(userId: UserId): Set<Permission>
}
