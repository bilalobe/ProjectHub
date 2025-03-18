package com.projecthub.base.security.authorization;

import com.projecthub.auth.service.RBACService;
import com.projecthub.base.user.domain.entity.AppUser;

/**
 * Service that enforces application-specific authorization rules.
 * This extends generic RBAC with domain-specific security logic.
 */
public class DomainAuthorizationService {

    private final RBACService rbacService;

    public DomainAuthorizationService(RBACService rbacService) {
        this.rbacService = rbacService;
    }

    /**
     * Determines if a user can update a specific project.
     *
     * @param userId The ID of the user attempting the action
     * @param projectId The ID of the project being accessed
     * @return true if the user is authorized, false otherwise
     */
    public boolean canUpdateProject(String userId, String projectId) {
        // Check if user is a project owner (domain-specific rule)
        boolean isProjectOwner = checkProjectOwnership(userId, projectId);
        
        // Check if user has admin permission (from RBAC)
        boolean hasAdminPermission = rbacService.hasPermission(userId, "project:update:any");
        
        // Either project ownership or admin permission grants access
        return isProjectOwner || hasAdminPermission;
    }
    
    /**
     * Determines if a user can view sensitive data within the application.
     *
     * @param userId The ID of the user attempting the action
     * @param dataType The type of sensitive data being accessed
     * @return true if the user is authorized, false otherwise
     */
    public boolean canViewSensitiveData(String userId, String dataType) {
        // Apply data classification rules
        if ("CONFIDENTIAL".equals(dataType)) {
            return rbacService.hasPermission(userId, "data:view:confidential");
        } else if ("RESTRICTED".equals(dataType)) {
            return rbacService.hasPermission(userId, "data:view:restricted");
        }
        
        // Non-sensitive data can be viewed by authenticated users
        return true;
    }
    
    /**
     * Checks if a user is the owner of a specific project.
     *
     * @param userId The ID of the user to check
     * @param projectId The ID of the project to check
     * @return true if the user owns the project, false otherwise
     */
    private boolean checkProjectOwnership(String userId, String projectId) {
        // Implementation would check project ownership in database
        // This is a placeholder for actual implementation
        return false;
    }
}