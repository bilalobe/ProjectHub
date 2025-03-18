package com.projecthub.base.security.facade;

import com.projecthub.auth.service.RBACService;
import com.projecthub.base.security.audit.SecurityAuditService;
import com.projecthub.base.security.audit.SecurityEventType;
import com.projecthub.base.security.authorization.DomainAuthorizationService;
import com.projecthub.base.user.domain.entity.AppUser;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Facade that provides unified access to security functionality across the foundation module.
 * Integrates authentication components from the auth module with domain-specific security
 * in the foundation module.
 */
@Component
public class SecurityFacade {

    private final RBACService rbacService;
    private final DomainAuthorizationService domainAuthorizationService;
    private final SecurityAuditService securityAuditService;

    public SecurityFacade(
            RBACService rbacService,
            DomainAuthorizationService domainAuthorizationService,
            SecurityAuditService securityAuditService) {
        this.rbacService = rbacService;
        this.domainAuthorizationService = domainAuthorizationService;
        this.securityAuditService = securityAuditService;
    }

    /**
     * Checks if a user has permission to perform an action on a project.
     *
     * @param userId The ID of the user
     * @param projectId The ID of the project
     * @param action The action being performed (create, read, update, delete)
     * @return true if the user has permission, false otherwise
     */
    public boolean hasProjectPermission(String userId, String projectId, String action) {
        // Apply domain-specific rules for project actions
        boolean hasPermission = false;
        
        if ("update".equals(action)) {
            hasPermission = domainAuthorizationService.canUpdateProject(userId, projectId);
        } else {
            // For other actions, defer to RBAC
            hasPermission = rbacService.hasPermission(userId, "project:" + action);
        }
        
        // Audit the access check
        if (!hasPermission) {
            securityAuditService.logSecurityEvent(
                SecurityEventType.ACCESS_DENIED,
                userId,
                "project",
                projectId,
                "Action: " + action
            );
        }
        
        return hasPermission;
    }

    /**
     * Checks if a user has permission to access sensitive data.
     *
     * @param userId The ID of the user
     * @param dataType The type of sensitive data
     * @return true if the user has permission, false otherwise
     */
    public boolean canAccessSensitiveData(String userId, String dataType) {
        boolean canAccess = domainAuthorizationService.canViewSensitiveData(userId, dataType);
        
        if (canAccess) {
            // Log successful access to sensitive data
            securityAuditService.logSecurityEvent(
                SecurityEventType.SENSITIVE_DATA_ACCESSED,
                userId,
                "data",
                dataType,
                "Successful access to sensitive data"
            );
        }
        
        return canAccess;
    }

    /**
     * Gets the roles assigned to a user.
     *
     * @param userId The ID of the user
     * @return The list of role names
     */
    public Set<String> getUserPermissions(String userId) {
        return rbacService.getUserPermissions(userId);
    }
}