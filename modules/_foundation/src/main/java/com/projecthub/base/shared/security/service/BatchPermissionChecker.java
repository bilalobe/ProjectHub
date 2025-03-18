package com.projecthub.base.shared.security.service;

import com.projecthub.base.shared.security.permission.Permission;
import org.apache.directory.fortress.core.model.Session;

import java.util.Map;
import java.util.Set;

/**
 * Interface for efficient batch permission checking.
 * Allows checking multiple permissions in a single operation to reduce overhead.
 */
public interface BatchPermissionChecker {
    /**
     * Check multiple permissions in a single batch operation
     *
     * @param session The user's session
     * @param permissions Set of permissions to check
     * @return Map of permissions to their grant status
     */
    Map<Permission, Boolean> checkPermissions(Session session, Set<Permission> permissions);

    /**
     * Preload permissions for a specific resource type to optimize subsequent checks
     *
     * @param resourceType The type of resource to preload permissions for
     */
    void preloadPermissions(String resourceType);
}
