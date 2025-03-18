package com.projecthub.base.school.infrastructure.security;

import com.projecthub.base.auth.service.fortress.FortressSessionService;
import com.projecthub.base.school.infrastructure.security.SchoolSecurityConfig.ObjectIdentifiers;
import com.projecthub.base.school.infrastructure.security.SchoolSecurityConfig.Operations;
import com.projecthub.base.shared.config.RbacSecurityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.model.Session;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Security service for the School module.
 * Enforces RBAC permissions at the service layer using Apache Fortress.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolSecurityService {

    private final AccessMgr accessManager;
    private final FortressSessionService sessionService;
    @Qualifier("")
    private final RbacSecurityConfig securityConfig;

    /**
     * Enforce "create" permission check for schools
     */
    public void enforceCreatePermission() {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.SCHOOL, Operations.CREATE)) {
            throw new AccessDeniedException("User does not have permission to create schools");
        }
    }

    /**
     * Enforce "read" permission check for a specific school
     *
     * @param schoolId The school identifier
     */
    public void enforceReadPermission(UUID schoolId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.SCHOOL, Operations.READ)) {
            throw new AccessDeniedException("User does not have permission to read school details");
        }
    }

    /**
     * Enforce "update" permission check for a specific school
     *
     * @param schoolId The school identifier
     */
    public void enforceUpdatePermission(UUID schoolId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.SCHOOL, Operations.UPDATE)) {
            throw new AccessDeniedException("User does not have permission to update school details");
        }
    }

    /**
     * Enforce "delete" permission check for a specific school
     *
     * @param schoolId The school identifier
     */
    public void enforceDeletePermission(UUID schoolId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.SCHOOL, Operations.DELETE)) {
            throw new AccessDeniedException("User does not have permission to delete schools");
        }
    }

    /**
     * Enforce "archive" permission check for a specific school
     *
     * @param schoolId The school identifier
     */
    public void enforceArchivePermission(UUID schoolId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.SCHOOL, Operations.ARCHIVE)) {
            throw new AccessDeniedException("User does not have permission to archive schools");
        }
    }

    /**
     * Get the current user's Fortress session from the security context
     *
     * @return The current Fortress session
     * @throws AccessDeniedException if no authenticated user is found
     */
    private Session getCurrentSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("No authenticated user found");
        }
        return sessionService.getSession(authentication);
    }
}
