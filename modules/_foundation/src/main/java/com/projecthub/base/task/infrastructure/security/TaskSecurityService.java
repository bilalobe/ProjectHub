package com.projecthub.base.task.infrastructure.security;

import com.projecthub.base.auth.service.fortress.FortressSessionService;
import com.projecthub.base.shared.config.RbacSecurityConfig;
import com.projecthub.base.task.infrastructure.security.TaskSecurityConfig.ObjectIdentifiers;
import com.projecthub.base.task.infrastructure.security.TaskSecurityConfig.Operations;
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
 * Security service for the Task module.
 * Enforces RBAC permissions at the service layer using Apache Fortress.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSecurityService {

    private final AccessMgr accessManager;
    private final FortressSessionService sessionService;
    @Qualifier("")
    private final RbacSecurityConfig securityConfig;

    /**
     * Enforce "create" permission check for tasks
     */
    public void enforceCreatePermission() {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.TASK, Operations.CREATE)) {
            throw new AccessDeniedException("User does not have permission to create tasks");
        }
    }

    /**
     * Enforce "read" permission check for a specific task
     *
     * @param taskId The task identifier
     */
    public void enforceReadPermission(UUID taskId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.TASK, Operations.READ)) {
            throw new AccessDeniedException("User does not have permission to read task details");
        }
    }

    /**
     * Enforce "update" permission check for a specific task
     *
     * @param taskId The task identifier
     */
    public void enforceUpdatePermission(UUID taskId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.TASK, Operations.UPDATE)) {
            throw new AccessDeniedException("User does not have permission to update task details");
        }
    }

    /**
     * Enforce "delete" permission check for a specific task
     *
     * @param taskId The task identifier
     */
    public void enforceDeletePermission(UUID taskId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.TASK, Operations.DELETE)) {
            throw new AccessDeniedException("User does not have permission to delete tasks");
        }
    }

    /**
     * Enforce "assign" permission check for a task
     *
     * @param taskId The task identifier
     */
    public void enforceAssignPermission(UUID taskId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.TASK_ASSIGNMENT, Operations.ASSIGN)) {
            throw new AccessDeniedException("User does not have permission to assign tasks");
        }
    }

    /**
     * Enforce "complete" permission check for a task
     *
     * @param taskId The task identifier
     */
    public void enforceCompletePermission(UUID taskId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.TASK, Operations.COMPLETE)) {
            throw new AccessDeniedException("User does not have permission to complete this task");
        }
    }

    /**
     * Enforce "review" permission check for a task
     *
     * @param taskId The task identifier
     */
    public void enforceReviewPermission(UUID taskId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.TASK, Operations.REVIEW)) {
            throw new AccessDeniedException("User does not have permission to review this task");
        }
    }

    /**
     * Enforce "comment" permission check for a task
     *
     * @param taskId The task identifier
     */
    public void enforceCommentPermission(UUID taskId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.TASK_COMMENT, Operations.COMMENT)) {
            throw new AccessDeniedException("User does not have permission to comment on this task");
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
