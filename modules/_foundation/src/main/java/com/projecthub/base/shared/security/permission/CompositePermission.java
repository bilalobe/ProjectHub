package com.projecthub.base.shared.security.permission;

import java.util.List;

/**
 * Interface for composite permissions that contain multiple sub-permissions.
 * Represents a higher-level permission that is composed of multiple atomic permissions.
 */
public interface CompositePermission extends Permission {
    
    /**
     * Gets all sub-permissions that make up this composite permission
     * 
     * @return List of component permissions
     */
    List<Permission> getSubPermissions();
    
    /**
     * Determines whether all sub-permissions are required for this permission to be granted
     * 
     * @return true if all permissions are required, false if any one is sufficient
     */
    boolean allRequired();
}