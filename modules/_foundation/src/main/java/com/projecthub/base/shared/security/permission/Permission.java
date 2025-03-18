package com.projecthub.base.shared.security.permission;

/**
 * Core interface for all permissions in the system.
 * Provides a unified way to represent and check permissions across all modules.
 */
public interface Permission {
    /**
     * Gets the type of resource this permission applies to
     * @return The resource type identifier
     */
    String getResourceType();
    
    /**
     * Gets the operation being performed
     * @return The operation identifier
     */
    String getOperation();
    
    /**
     * Whether this permission requires ownership of the resource
     * @return true if ownership is required, false otherwise
     */
    boolean requiresOwnership();
    
    /**
     * Whether this is an administrative permission
     * @return true if this is an administrative permission
     */
    boolean isAdministrative();
    
    /**
     * Gets the target resource identifier if applicable
     * @return The resource ID or null if not applicable
     */
    String getResourceId();
}