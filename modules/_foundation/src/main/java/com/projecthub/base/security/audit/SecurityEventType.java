package com.projecthub.base.security.audit;

/**
 * Enum representing different types of security events to be logged.
 * Used to categorize security audit logs.
 */
public enum SecurityEventType {
    // Authentication-related events
    LOGIN_SUCCESS("User successfully logged in"),
    LOGIN_FAILURE("Login attempt failed"),
    LOGOUT("User logged out"),
    PASSWORD_CHANGED("User changed password"),
    PASSWORD_RESET_REQUESTED("Password reset was requested"),
    MFA_ENABLED("Multi-factor authentication was enabled"),
    MFA_DISABLED("Multi-factor authentication was disabled"),
    
    // Authorization-related events
    ACCESS_DENIED("Access to a resource was denied"),
    PERMISSION_GRANTED("Permission was granted to user"),
    PERMISSION_REVOKED("Permission was revoked from user"),
    ROLE_ASSIGNED("Role was assigned to user"),
    ROLE_REMOVED("Role was removed from user"),
    
    // Data access events
    SENSITIVE_DATA_ACCESSED("Sensitive data was accessed"),
    DATA_EXPORTED("Data was exported from the system"),
    
    // Admin activities
    USER_CREATED("New user account was created"),
    USER_DELETED("User account was deleted"),
    USER_LOCKED("User account was locked"),
    USER_UNLOCKED("User account was unlocked"),
    
    // System events
    SECURITY_POLICY_CHANGED("Security policy was changed"),
    SUSPICIOUS_ACTIVITY_DETECTED("Suspicious activity was detected");
    
    private final String description;
    
    SecurityEventType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return this.description;
    }
}