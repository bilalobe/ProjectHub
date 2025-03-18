package com.projecthub.auth.api.dto;

/**
 * Data transfer object representing a permission in the RBAC system.
 * This is used to transfer permission information between modules.
 */
public record PermissionDTO(
    String objectType,
    String operation,
    String objectId
) {
    public static PermissionDTO of(String objectType, String operation) {
        return new PermissionDTO(objectType, operation, null);
    }

    public static PermissionDTO of(String objectType, String operation, String objectId) {
        return new PermissionDTO(objectType, operation, objectId);
    }
}