package com.projecthub.base.security.service;

import com.projecthub.base.cohort.domain.entity.Cohort;
import com.projecthub.base.security.permission.CohortPermissions;
import com.projecthub.base.shared.domain.enums.security.RoleType;
import com.projecthub.base.shared.domain.enums.security.SecurityAuditAction;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Security service for seating management operations.
 * Provides methods to check permissions related to seating configuration, management, and IoT device control.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeatingSecurityService {

    private final SecurityService securityService;
    private final SecurityAuditService auditService;

    /**
     * Checks if the current user has permission to view seating arrangements for a cohort.
     *
     * @param cohortId The cohort ID
     * @return true if the user has permission, false otherwise
     */
    public boolean canViewSeating(UUID cohortId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return hasAnyPermission(CohortPermissions.VIEW_SEATING, CohortPermissions.MANAGE_SEATING) ||
               securityService.hasRole(RoleType.TEACHER.name()) ||
               securityService.hasRole(RoleType.ADMIN.name());
    }

    /**
     * Enforces permission to view seating arrangements for a cohort.
     * Throws an UnauthorizedException if the user lacks permission.
     *
     * @param cohortId The cohort ID
     * @throws UnauthorizedException if the user lacks permission
     */
    public void enforceViewSeating(UUID cohortId) {
        if (!canViewSeating(cohortId)) {
            logAndThrowUnauthorizedException("User lacks permission to view seating arrangements", cohortId);
        }
    }

    /**
     * Checks if the current user has permission to configure seating arrangements.
     *
     * @param cohortId The cohort ID
     * @return true if the user has permission, false otherwise
     */
    public boolean canConfigureSeating(UUID cohortId) {
        boolean hasPermission = hasAnyPermission(CohortPermissions.CONFIGURE_SEATING, CohortPermissions.MANAGE_COHORTS) || 
               securityService.hasRole(RoleType.ADMIN.name());
        
        // Also check if the user is a teacher assigned to this cohort
        if (!hasPermission) {
            hasPermission = securityService.hasRole(RoleType.TEACHER.name()) && 
                          securityService.isCurrentUserCohortOwner(cohortId);
        }
        
        return hasPermission;
    }

    /**
     * Enforces permission to configure seating arrangements.
     * Throws an UnauthorizedException if the user lacks permission.
     *
     * @param cohortId The cohort ID
     * @throws UnauthorizedException if the user lacks permission
     */
    public void enforceConfigureSeating(UUID cohortId) {
        if (!canConfigureSeating(cohortId)) {
            logAndThrowUnauthorizedException("User lacks permission to configure seating", cohortId);
        }
    }

    /**
     * Checks if the current user has permission to assign team seating positions.
     *
     * @param cohortId The cohort ID
     * @return true if the user has permission, false otherwise
     */
    public boolean canAssignSeating(UUID cohortId) {
        return hasAnyPermission(
            CohortPermissions.ASSIGN_SEATS, 
            CohortPermissions.MANAGE_SEATING, 
            CohortPermissions.MANAGE_COHORTS
        ) || securityService.hasRole(RoleType.TEACHER.name()) || securityService.hasRole(RoleType.ADMIN.name());
    }

    /**
     * Enforces permission to assign team seating positions.
     * Throws an UnauthorizedException if the user lacks permission.
     *
     * @param cohortId The cohort ID
     * @throws UnauthorizedException if the user lacks permission
     */
    public void enforceAssignSeating(UUID cohortId) {
        if (!canAssignSeating(cohortId)) {
            logAndThrowUnauthorizedException("User lacks permission to assign seating positions", cohortId);
        }
    }

    /**
     * Checks if the current user can create custom seating layouts.
     * This is a higher privilege operation limited to administrators and cohort owners who are teachers.
     *
     * @param cohortId The cohort ID this layout would be for
     * @return true if the user has permission, false otherwise
     */
    public boolean canCreateCustomLayout(UUID cohortId) {
        boolean hasPermission = hasPermission(CohortPermissions.CREATE_CUSTOM_LAYOUT) || 
               securityService.hasRole(RoleType.ADMIN.name());
        
        // Teacher privilege to override map schema for their cohorts
        if (!hasPermission && securityService.hasRole(RoleType.TEACHER.name())) {
            hasPermission = securityService.isCurrentUserCohortOwner(cohortId);
        }
        
        return hasPermission;
    }

    /**
     * Enforces permission to create custom seating layouts.
     * Throws an UnauthorizedException if the user lacks permission.
     * This overload checks if a specific teacher has permission for a specific cohort.
     *
     * @param cohortId The cohort ID this layout would be for
     * @throws UnauthorizedException if the user lacks permission
     */
    public void enforceCreateCustomLayout(UUID cohortId) {
        if (!canCreateCustomLayout(cohortId)) {
            auditService.logSecurityEvent(
                cohortId, 
                SecurityAuditAction.ACCESS_DENIED, 
                "User attempted to create custom seating layout without permission"
            );
            throw new UnauthorizedException("You do not have permission to create custom seating layouts for this cohort");
        }
    }
    
    /**
     * Backwards compatibility method without cohortId.
     * Use the version with cohortId parameter when possible.
     * 
     * @throws UnauthorizedException if the user lacks permission
     */
    public void enforceCreateCustomLayout() {
        if (!hasPermission(CohortPermissions.CREATE_CUSTOM_LAYOUT) && 
            !securityService.hasRole(RoleType.ADMIN.name())) {
            
            auditService.logSecurityEvent(
                null, 
                SecurityAuditAction.ACCESS_DENIED, 
                "User attempted to create custom seating layout without permission"
            );
            throw new UnauthorizedException("You do not have permission to create custom seating layouts");
        }
    }
    
    /**
     * Checks if the current user has permission to view IoT devices.
     *
     * @param cohortId The cohort ID
     * @return true if the user has permission, false otherwise
     */
    public boolean canViewIoTDevices(UUID cohortId) {
        return hasAnyPermission(
            CohortPermissions.VIEW_IOT_DEVICES, 
            CohortPermissions.MANAGE_IOT_DEVICES, 
            CohortPermissions.CONTROL_IOT_DEVICES
        ) || securityService.hasAnyRole(RoleType.TEACHER.name(), RoleType.ADMIN.name());
    }

    /**
     * Enforces permission to view IoT devices.
     * Throws an UnauthorizedException if the user lacks permission.
     *
     * @param cohortId The cohort ID
     * @throws UnauthorizedException if the user lacks permission
     */
    public void enforceViewIoTDevices(UUID cohortId) {
        if (!canViewIoTDevices(cohortId)) {
            logAndThrowUnauthorizedException("User lacks permission to view IoT devices", cohortId);
        }
    }

    /**
     * Checks if the current user has permission to manage IoT devices.
     *
     * @param cohortId The cohort ID
     * @return true if the user has permission, false otherwise
     */
    public boolean canManageIoTDevices(UUID cohortId) {
        return hasPermission(CohortPermissions.MANAGE_IOT_DEVICES) || 
               securityService.hasRole(RoleType.ADMIN.name());
    }

    /**
     * Enforces permission to manage IoT devices.
     * Throws an UnauthorizedException if the user lacks permission.
     *
     * @param cohortId The cohort ID
     * @throws UnauthorizedException if the user lacks permission
     */
    public void enforceManageIoTDevices(UUID cohortId) {
        if (!canManageIoTDevices(cohortId)) {
            logAndThrowUnauthorizedException("User lacks permission to manage IoT devices", cohortId);
        }
    }

    /**
     * Checks if the current user has permission to control IoT devices.
     *
     * @param cohortId The cohort ID
     * @return true if the user has permission, false otherwise
     */
    public boolean canControlIoTDevices(UUID cohortId) {
        return hasAnyPermission(
            CohortPermissions.CONTROL_IOT_DEVICES, 
            CohortPermissions.MANAGE_IOT_DEVICES
        ) || securityService.hasAnyRole(RoleType.TEACHER.name(), RoleType.ADMIN.name());
    }

    /**
     * Enforces permission to control IoT devices.
     * Throws an UnauthorizedException if the user lacks permission.
     *
     * @param cohortId The cohort ID
     * @throws UnauthorizedException if the user lacks permission
     */
    public void enforceControlIoTDevices(UUID cohortId) {
        if (!canControlIoTDevices(cohortId)) {
            logAndThrowUnauthorizedException("User lacks permission to control IoT devices", cohortId);
        }
    }

    /**
     * Checks if the current user can override the seating map schema.
     * Admins can always override, teachers can override for their own cohorts.
     *
     * @param cohortId The cohort ID
     * @return true if the user has permission to override the map schema
     */
    public boolean canOverrideMapSchema(UUID cohortId) {
        boolean isAdmin = securityService.hasRole(RoleType.ADMIN.name());
        boolean isTeacherOfCohort = securityService.hasRole(RoleType.TEACHER.name()) && 
                                  securityService.isCurrentUserCohortOwner(cohortId);
        
        return isAdmin || isTeacherOfCohort;
    }
    
    /**
     * Enforces permission to override the seating map schema.
     * 
     * @param cohortId The cohort ID
     * @throws UnauthorizedException if the user lacks permission
     */
    public void enforceOverrideMapSchema(UUID cohortId) {
        if (!canOverrideMapSchema(cohortId)) {
            logAndThrowUnauthorizedException(
                "User lacks permission to override seating map schema",
                cohortId
            );
        }
    }

    // Helper methods
    private boolean hasPermission(String permission) {
        return securityService.hasPermission(permission);
    }

    private boolean hasAnyPermission(String... permissions) {
        return securityService.hasAnyPermission(permissions);
    }

    private void logAndThrowUnauthorizedException(String message, UUID entityId) {
        auditService.logSecurityEvent(
            entityId, 
            SecurityAuditAction.ACCESS_DENIED, 
            message
        );
        throw new UnauthorizedException(message);
    }
}