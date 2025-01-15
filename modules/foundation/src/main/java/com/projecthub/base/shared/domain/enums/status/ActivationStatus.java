package com.projecthub.base.shared.domain.enums.status;

public class ActivationStatus {
    private ActivationStatus() {
        // private constructor to hide the implicit public one
    }
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";
    public static final String PENDING = "PENDING";
    public static final String DELETED = "DELETED";
    public static final String SUSPENDED = "SUSPENDED";
    public static final String REJECTED = "REJECTED";
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED_BY_ADMIN = "REJECTED_BY_ADMIN";
    public static final String REJECTED_BY_USER = "REJECTED_BY_USER";
    public static final String APPROVED_BY_ADMIN = "APPROVED_BY_ADMIN";
    public static final String APPROVED_BY_USER = "APPROVED_BY_USER";
    public static final String SUSPENDED_BY_ADMIN = "SUSPENDED_BY_ADMIN";
    public static final String SUSPENDED_BY_USER = "SUSPENDED_BY_USER";
    public static final String DELETED_BY_ADMIN = "DELETED_BY_ADMIN";
    public static final String DELETED_BY_USER = "DELETED_BY_USER";
    public static final String PENDING_BY_ADMIN = "PENDING_BY_ADMIN";
    public static final String PENDING_BY_USER = "PENDING_BY_USER";
    public static final String ACTIVE_BY_ADMIN = "ACTIVE_BY_ADMIN";
    public static final String ACTIVE_BY_USER = "ACTIVE_BY_USER";
    public static final String INACTIVE_BY_ADMIN = "INACTIVE_BY_ADMIN";
    public static final String INACTIVE_BY_USER = "INACTIVE_BY_USER";
    public static final String REJECTED_BY_SYSTEM = "REJECTED_BY_SYSTEM";
    public static final String APPROVED_BY_SYSTEM = "APPROVED_BY_SYSTEM";
    public static final String SUSPENDED_BY_SYSTEM = "SUSPENDED_BY_SYSTEM";
    public static final String DELETED_BY_SYSTEM = "DELETED_BY_SYSTEM";
    public static final String PENDING_BY_SYSTEM = "PENDING_BY_SYSTEM";
    public static final String ACTIVE_BY_SYSTEM = "ACTIVE_BY_SYSTEM";
    public static final String INACTIVE_BY_SYSTEM = "INACTIVE_BY_SYSTEM";
    public static final String SUSPENDED_BY_USER_AND_ADMIN = "SUSPENDED_BY_USER_AND_ADMIN";
    public static final String REJECTED_BY_USER_AND_ADMIN = "REJECTED_BY_USER_AND_ADMIN";
    public static final String DELETED_BY_USER_AND_ADMIN = "DELETED_BY_USER_AND_ADMIN";
    public static final String PENDING_BY_USER_AND_ADMIN = "PENDING_BY_USER_AND_ADMIN";
    
}
