package com.projecthub.base.shared.security.permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Factory class for building permissions with a fluent API.
 * Makes it easy to create both simple and composite permissions.
 */
public class PermissionBuilder {
    private String resourceType;
    private String operation;
    private String resourceId;
    private boolean requiresOwnership;
    private boolean administrative;
    private List<Permission> subPermissions;
    private boolean composite;
    private boolean allRequired = true;

    private PermissionBuilder() {
        this.subPermissions = new ArrayList<>();
    }

    public static PermissionBuilder forResource(String resourceType) {
        PermissionBuilder builder = new PermissionBuilder();
        builder.resourceType = resourceType;
        return builder;
    }

    public PermissionBuilder operation(String operation) {
        this.operation = operation;
        return this;
    }

    public PermissionBuilder resourceId(String resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public PermissionBuilder requireOwnership() {
        this.requiresOwnership = true;
        return this;
    }

    public PermissionBuilder administrative() {
        this.administrative = true;
        return this;
    }

    public PermissionBuilder withSubPermissions(Permission... permissions) {
        this.composite = true;
        this.subPermissions.addAll(Arrays.asList(permissions));
        return this;
    }

    public PermissionBuilder requireAll() {
        this.allRequired = true;
        return this;
    }

    public PermissionBuilder requireAny() {
        this.allRequired = false;
        return this;
    }

    public Permission build() {
        if (!composite) {
            return SimplePermission.builder()
                .resourceType(resourceType)
                .operation(operation)
                .resourceId(resourceId)
                .requiresOwnership(requiresOwnership)
                .administrative(administrative)
                .build();
        } else {
            return SimpleCompositePermission.builder()
                .resourceType(resourceType)
                .operation(operation)
                .resourceId(resourceId)
                .requiresOwnership(requiresOwnership)
                .administrative(administrative)
                .allRequired(allRequired)
                .subPermissions(subPermissions)
                .build();
        }
    }
}