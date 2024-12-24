package com.projecthub.core.enums;

/**
 * Represents the different types of roles within the system.
 */
public enum RoleType {
    /** Student role with limited access */
    STUDENT,
    /** Administrator with full permissions */
    ADMIN,
    /** Teacher role with specific privileges */
    TEACHER,
    /** Moderator with content oversight capabilities */
    MODERATOR,
    /** Guest role with limited access, read-only */
    GUEST;
}