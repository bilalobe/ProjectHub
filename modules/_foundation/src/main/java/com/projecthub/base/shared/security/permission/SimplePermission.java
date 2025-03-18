package com.projecthub.base.shared.security.permission;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Simple implementation of the Permission interface.
 * This class provides a basic permission implementation that can be used
 * throughout the application.
 */
@Getter
@Builder
@ToString
public class SimplePermission implements Permission {
    private final String resourceType;
    private final String operation;
    private final String resourceId;
    
    @Builder.Default
    private final boolean requiresOwnership = false;
    
    @Builder.Default
    private final boolean administrative = false;
    
    @Override
    public boolean requiresOwnership() {
        return requiresOwnership;
    }
    
    @Override
    public boolean isAdministrative() {
        return administrative;
    }
    
    @Override
    public String getResourceId() {
        return resourceId;
    }
    
    /**
     * Creates a new permission for a specific operation on a resource type
     */
    public static SimplePermission of(String resourceType, String operation) {
        return SimplePermission.builder()
            .resourceType(resourceType)
            .operation(operation)
            .build();
    }
    
    /**
     * Creates a new permission for a specific operation on a specific resource
     */
    public static SimplePermission of(String resourceType, String operation, String resourceId) {
        return SimplePermission.builder()
            .resourceType(resourceType)
            .operation(operation)
            .resourceId(resourceId)
            .build();
    }
    
    /**
     * Creates a new administrative permission
     */
    public static SimplePermission administrative(String resourceType, String operation) {
        return SimplePermission.builder()
            .resourceType(resourceType)
            .operation(operation)
            .administrative(true)
            .build();
    }
    
    /**
     * Creates a new ownership-based permission
     */
    public static SimplePermission ownerOnly(String resourceType, String operation, String resourceId) {
        return SimplePermission.builder()
            .resourceType(resourceType)
            .operation(operation)
            .resourceId(resourceId)
            .requiresOwnership(true)
            .build();
    }
}