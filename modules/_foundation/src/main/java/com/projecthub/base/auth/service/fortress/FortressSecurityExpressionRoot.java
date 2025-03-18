package com.projecthub.base.auth.service.fortress;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * Custom security expression root for Apache Fortress.
 * This class adds custom security expressions for Fortress permissions that can be used
 * in @PreAuthorize annotations.
 * <p>
 * Examples:
 * @PreAuthorize("hasObjectPermission('submission', 'grade')")
 * @PreAuthorize("hasAnyFortressRole('ADMIN', 'INSTRUCTOR')")
 */
public class FortressSecurityExpressionRoot extends SecurityExpressionRoot
        implements MethodSecurityExpressionOperations {

    private final FortressAccessControlService accessControlService;
    private Object filterObject = null;
    private Object returnObject = null;
    private Object target = null;

    public FortressSecurityExpressionRoot(Authentication authentication,
                                        FortressAccessControlService accessControlService) {
        super(authentication);
        this.accessControlService = accessControlService;
    }

    /**
     * Check if the current user has the specified object permission.
     * Maps to Fortress permission model of object:operation.
     *
     * @param objectName the object name (e.g., "submission")
     * @param operation the operation (e.g., "grade")
     * @return true if the user has the permission
     */
    public boolean hasObjectPermission(String objectName, String operation) {
        return accessControlService.hasPermission(objectName, operation);
    }

    /**
     * Check if the current user has any of the specified object permissions.
     *
     * @param permissions array of object:operation pairs
     * @return true if the user has any of the permissions
     */
    public boolean hasAnyObjectPermission(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }

        for (String permission : permissions) {
            String[] parts = permission.split(":");
            if (parts.length == 2) {
                if (accessControlService.hasPermission(parts[0], parts[1])) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if the current user has any of the specified Fortress roles.
     *
     * @param roles array of role names
     * @return true if the user has any of the roles
     */
    public boolean hasAnyFortressRole(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }

        for (String role : roles) {
            if (FortressAccessControlService.hasRole(role)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    void setThis(Object target) {
        this.target = target;
    }
}
