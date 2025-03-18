package com.projecthub.base.milestone.infrastructure.security;

import com.projecthub.base.auth.service.fortress.FortressSessionService;
import com.projecthub.base.milestone.infrastructure.security.MilestoneSecurityConfig.ObjectIdentifiers;
import com.projecthub.base.milestone.infrastructure.security.MilestoneSecurityConfig.Operations;
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
 * Security service for the Milestone module.
 * Enforces RBAC permissions at the service layer using Apache Fortress.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MilestoneSecurityService {

    private final AccessMgr accessManager;
    private final FortressSessionService sessionService;
    @Qualifier("")
    private final RbacSecurityConfig securityConfig;

    /**
     * Enforce "create" permission check for milestones
     */
    public void enforceCreatePermission() {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.MILESTONE, Operations.CREATE)) {
            throw new AccessDeniedException("User does not have permission to create milestones");
        }
    }

    /**
     * Enforce "read" permission check for a specific milestone
     *
     * @param milestoneId The milestone identifier
     */
    public void enforceReadPermission(UUID milestoneId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.MILESTONE, Operations.READ)) {
            throw new AccessDeniedException("User does not have permission to read milestone details");
        }
    }

    /**
     * Enforce "update" permission check for a specific milestone
     *
     * @param milestoneId The milestone identifier
     */
    public void enforceUpdatePermission(UUID milestoneId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.MILESTONE, Operations.UPDATE)) {
            throw new AccessDeniedException("User does not have permission to update milestone details");
        }
    }

    /**
     * Enforce "delete" permission check for a specific milestone
     *
     * @param milestoneId The milestone identifier
     */
    public void enforceDeletePermission(UUID milestoneId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.MILESTONE, Operations.DELETE)) {
            throw new AccessDeniedException("User does not have permission to delete milestones");
        }
    }

    /**
     * Enforce "publish" permission check for a milestone
     *
     * @param milestoneId The milestone identifier
     */
    public void enforcePublishPermission(UUID milestoneId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.MILESTONE, Operations.PUBLISH)) {
            throw new AccessDeniedException("User does not have permission to publish milestones");
        }
    }

    /**
     * Enforce "unpublish" permission check for a milestone
     *
     * @param milestoneId The milestone identifier
     */
    public void enforceUnpublishPermission(UUID milestoneId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.MILESTONE, Operations.UNPUBLISH)) {
            throw new AccessDeniedException("User does not have permission to unpublish milestones");
        }
    }

    /**
     * Enforce "extend" permission check for a milestone
     *
     * @param milestoneId The milestone identifier
     */
    public void enforceExtendPermission(UUID milestoneId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.MILESTONE_SCHEDULE, Operations.EXTEND)) {
            throw new AccessDeniedException("User does not have permission to extend milestone deadlines");
        }
    }

    /**
     * Enforce "mark completed" permission check for a milestone
     *
     * @param milestoneId The milestone identifier
     */
    public void enforceMarkCompletedPermission(UUID milestoneId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.MILESTONE, Operations.MARK_COMPLETED)) {
            throw new AccessDeniedException("User does not have permission to mark milestones as completed");
        }
    }

    /**
     * Enforce "add criteria" permission check for a milestone
     *
     * @param milestoneId The milestone identifier
     */
    public void enforceAddCriteriaPermission(UUID milestoneId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.MILESTONE_CRITERIA, Operations.ADD_CRITERIA)) {
            throw new AccessDeniedException("User does not have permission to add criteria to milestones");
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
