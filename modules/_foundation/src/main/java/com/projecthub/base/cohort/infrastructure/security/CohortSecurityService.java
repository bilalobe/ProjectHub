package com.projecthub.base.cohort.infrastructure.security;

import com.projecthub.base.auth.service.fortress.FortressSessionService;
import com.projecthub.base.cohort.infrastructure.security.CohortSecurityConfig.ObjectIdentifiers;
import com.projecthub.base.cohort.infrastructure.security.CohortSecurityConfig.Operations;
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
 * Security service for the Cohort module.
 * Enforces RBAC permissions at the service layer using Apache Fortress.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CohortSecurityService {

    private final AccessMgr accessManager;
    private final FortressSessionService sessionService;
    @Qualifier("")
    private final RbacSecurityConfig securityConfig;

    /**
     * Enforce "create" permission check for cohorts
     */
    public void enforceCreatePermission() {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.COHORT, Operations.CREATE)) {
            throw new AccessDeniedException("User does not have permission to create cohorts");
        }
    }

    /**
     * Enforce "read" permission check for a specific cohort
     *
     * @param cohortId The cohort identifier
     */
    public void enforceReadPermission(UUID cohortId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.COHORT, Operations.READ)) {
            throw new AccessDeniedException("User does not have permission to read cohort details");
        }
    }

    /**
     * Enforce "update" permission check for a specific cohort
     *
     * @param cohortId The cohort identifier
     */
    public void enforceUpdatePermission(UUID cohortId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.COHORT, Operations.UPDATE)) {
            throw new AccessDeniedException("User does not have permission to update cohort details");
        }
    }

    /**
     * Enforce "delete" permission check for a specific cohort
     *
     * @param cohortId The cohort identifier
     */
    public void enforceDeletePermission(UUID cohortId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.COHORT, Operations.DELETE)) {
            throw new AccessDeniedException("User does not have permission to delete cohorts");
        }
    }

    /**
     * Enforce "add team" permission check for a specific cohort
     *
     * @param cohortId The cohort identifier
     */
    public void enforceAddTeamPermission(UUID cohortId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.COHORT, Operations.ADD_TEAM)) {
            throw new AccessDeniedException("User does not have permission to add teams to cohort");
        }
    }

    /**
     * Enforce "remove team" permission check for a specific cohort
     *
     * @param cohortId The cohort identifier
     */
    public void enforceRemoveTeamPermission(UUID cohortId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.COHORT, Operations.REMOVE_TEAM)) {
            throw new AccessDeniedException("User does not have permission to remove teams from cohort");
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
