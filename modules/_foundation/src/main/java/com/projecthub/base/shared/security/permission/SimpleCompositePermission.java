package com.projecthub.base.shared.security.permission;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

/**
 * Simple implementation of the CompositePermission interface.
 * This class provides a way to combine multiple permissions into a single permission check.
 */
@Getter
@Builder
public class SimpleCompositePermission implements CompositePermission {
    private final String resourceType;
    private final String operation;
    private final String resourceId;
    
    @Builder.Default
    private final boolean requiresOwnership = false;
    
    @Builder.Default
    private final boolean administrative = false;
    
    @Builder.Default
    private final boolean allRequired = true;
    
    @Singular
    private final List<Permission> subPermissions;
    
    @Override
    public boolean requiresOwnership() {
        return requiresOwnership;
    }
    
    @Override
    public boolean isAdministrative() {
        return administrative;
    }
    
    @Override
    public boolean allRequired() {
        return allRequired;
    }
    
    /**
     * Creates a new composite permission that requires all sub-permissions
     */
    public static SimpleCompositePermission requireAll(String resourceType, String operation, List<Permission> permissions) {
        return SimpleCompositePermission.builder()
            .resourceType(resourceType)
            .operation(operation)
            .subPermissions(permissions)
            .allRequired(true)
            .build();
    }
    
    /**
     * Creates a new composite permission that requires any sub-permission
     */
    public static SimpleCompositePermission requireAny(String resourceType, String operation, List<Permission> permissions) {
        return SimpleCompositePermission.builder()
            .resourceType(resourceType)
            .operation(operation)
            .subPermissions(permissions)
            .allRequired(false)
            .build();
    }
    
    /**
     * Creates a new administrative composite permission
     */
    public static SimpleCompositePermission administrative(String resourceType, String operation, List<Permission> permissions) {
        return SimpleCompositePermission.builder()
            .resourceType(resourceType)
            .operation(operation)
            .subPermissions(permissions)
            .administrative(true)
            .build();
    }
}