package com.projecthub.core.models;

public enum SecurityAuditAction {
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    LOGOUT,
    PASSWORD_CHANGE,
    PASSWORD_RESET_REQUEST,
    PASSWORD_RESET_COMPLETE,
    ACCOUNT_LOCKED,
    ACCOUNT_UNLOCKED,
    ACCOUNT_DISABLED,
    ACCOUNT_ENABLED,
    PROFILE_UPDATE,
    ROLE_CHANGE,
    ACCESS_DENIED,
    TOKEN_REFRESH,
    MFA_ENABLED,
    MFA_DISABLED,
    API_KEY_GENERATED,
    API_KEY_REVOKED
}
