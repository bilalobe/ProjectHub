package com.projecthub.base.project.infrastructure.security;

import com.projecthub.base.auth.service.fortress.FortressSessionService;
import com.projecthub.base.project.infrastructure.security.ProjectSecurityConfig.ObjectIdentifiers;
import com.projecthub.base.project.infrastructure.security.ProjectSecurityConfig.Operations;
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
 * Security service for the Project module.
 * Enforces RBAC permissions at the service layer using Apache Fortress.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectSecurityService {

    private final AccessMgr accessManager;
    private final FortressSessionService sessionService;
    @Qualifier("")
    private final RbacSecurityConfig securityConfig;

    /**
     * Enforce "create" permission check for projects
     */
    public void enforceCreatePermission() {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.PROJECT, Operations.CREATE)) {
            throw new AccessDeniedException("User does not have permission to create projects");
        }
    }

    /**
     * Enforce "read" permission check for a specific project
     *
     * @param projectId The project identifier
     */
    public void enforceReadPermission(UUID projectId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.PROJECT, Operations.READ)) {
            throw new AccessDeniedException("User does not have permission to read project details");
        }
    }

    /**
     * Enforce "update" permission check for a specific project
     *
     * @param projectId The project identifier
     */
    public void enforceUpdatePermission(UUID projectId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.PROJECT, Operations.UPDATE)) {
            throw new AccessDeniedException("User does not have permission to update project details");
        }
    }

    /**
     * Enforce "delete" permission check for a specific project
     *
     * @param projectId The project identifier
     */
    public void enforceDeletePermission(UUID projectId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.PROJECT, Operations.DELETE)) {
            throw new AccessDeniedException("User does not have permission to delete projects");
        }
    }

    /**
     * Enforce "publish" permission check for a specific project
     *
     * @param projectId The project identifier
     */
    public void enforcePublishPermission(UUID projectId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.PROJECT, Operations.PUBLISH)) {
            throw new AccessDeniedException("User does not have permission to publish projects");
        }
    }

    /**
     * Enforce "add milestone" permission check for a specific project
     *
     * @param projectId The project identifier
     */
    public void enforceAddMilestonePermission(UUID projectId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.PROJECT_MILESTONE, Operations.ADD_MILESTONE)) {
            throw new AccessDeniedException("User does not have permission to add milestones to project");
        }
    }

    /**
     * Enforce "add task" permission check for a specific project
     *
     * @param projectId The project identifier
     */
    public void enforceAddTaskPermission(UUID projectId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.PROJECT_TASK, Operations.ADD_TASK)) {
            throw new AccessDeniedException("User does not have permission to add tasks to project");
        }
    }

    /**
     * Enforce "grade" permission check for a specific project
     *
     * @param projectId The project identifier
     */
    public void enforceGradePermission(UUID projectId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.PROJECT, Operations.GRADE)) {
            throw new AccessDeniedException("User does not have permission to grade projects");
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
