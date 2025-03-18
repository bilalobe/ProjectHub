package com.projecthub.base.shared.security.domain.audit;

/**
 * Enum representing various security audit actions that can be performed by a user or system.
 */
public enum SecurityAuditAction {
    /**
     * Successful login attempt
     */
    LOGIN_SUCCESS,
    /**
     * Failed login attempt
     */
    LOGIN_FAILURE,
    /**
     * User logged out
     */
    LOGOUT,
    /**
     * User changed their password
     */
    PASSWORD_CHANGE,
    /**
     * User requested a password reset
     */
    PASSWORD_RESET_REQUEST,
    /**
     * User completed a password reset
     */
    PASSWORD_RESET_COMPLETE,
    /**
     * User account was locked
     */
    ACCOUNT_LOCKED,
    /**
     * User account was unlocked
     */
    ACCOUNT_UNLOCKED,
    /**
     * User account was disabled
     */
    ACCOUNT_DISABLED,
    /**
     * User account was enabled
     */
    ACCOUNT_ENABLED,
    /**
     * User updated their profile
     */
    PROFILE_UPDATE,
    /**
     * User role was changed
     */
    ROLE_CHANGE,
    /**
     * Access was denied to the user
     */
    ACCESS_DENIED,
    /**
     * Token was refreshed
     */
    TOKEN_REFRESH,
    /**
     * Multi-factor authentication was enabled
     */
    MFA_ENABLED,
    /**
     * Multi-factor authentication was disabled
     */
    MFA_DISABLED,
    /**
     * API key was generated
     */
    API_KEY_GENERATED,
    /**
     * API key was revoked
     */
    API_KEY_REVOKED,
    /**
     * Password validation failed
     */
    PASSWORD_VALIDATION_FAILURE,
    /**
     * Password was successfully changed
     */
    PASSWORD_CHANGED,
    /**
     * Password change attempt failed
     */
    PASSWORD_CHANGE_FAILED,
    /**
     * All user sessions were invalidated
     */
    SESSIONS_INVALIDATED,
    /**
     * User account was rate limited
     */
    ACCOUNT_RATE_LIMITED
}