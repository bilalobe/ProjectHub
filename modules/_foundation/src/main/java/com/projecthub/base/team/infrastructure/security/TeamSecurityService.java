package com.projecthub.base.team.infrastructure.security;

import com.projecthub.base.auth.service.fortress.FortressSessionService;
import com.projecthub.base.shared.config.RbacSecurityConfig;
import com.projecthub.base.team.infrastructure.security.TeamSecurityConfig.ObjectIdentifiers;
import com.projecthub.base.team.infrastructure.security.TeamSecurityConfig.Operations;
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
 * Security service for the Team module.
 * Enforces RBAC permissions at the service layer using Apache Fortress.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TeamSecurityService {

    private final AccessMgr accessManager;
    private final FortressSessionService sessionService;
    @Qualifier("")
    private final RbacSecurityConfig securityConfig;

    /**
     * Enforce "create" permission check for teams
     */
    public void enforceCreatePermission() {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.TEAM, Operations.CREATE)) {
            throw new AccessDeniedException("User does not have permission to create teams");
        }
    }

    /**
     * Enforce "read" permission check for a specific team
     *
     * @param teamId The team identifier
     */
    public void enforceReadPermission(UUID teamId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.TEAM, Operations.READ)) {
            throw new AccessDeniedException("User does not have permission to read team details");
        }
    }

    /**
     * Enforce "update" permission check for a specific team
     *
     * @param teamId The team identifier
     */
    public void enforceUpdatePermission(UUID teamId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.TEAM, Operations.UPDATE)) {
            throw new AccessDeniedException("User does not have permission to update team details");
        }
    }

    /**
     * Enforce "delete" permission check for a specific team
     *
     * @param teamId The team identifier
     */
    public void enforceDeletePermission(UUID teamId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.TEAM, Operations.DELETE)) {
            throw new AccessDeniedException("User does not have permission to delete teams");
        }
    }

    /**
     * Enforce "add member" permission check for a specific team
     *
     * @param teamId The team identifier
     */
    public void enforceAddMemberPermission(UUID teamId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.TEAM_MEMBERSHIP, Operations.ADD_MEMBER)) {
            throw new AccessDeniedException("User does not have permission to add members to team");
        }
    }

    /**
     * Enforce "remove member" permission check for a specific team
     *
     * @param teamId The team identifier
     */
    public void enforceRemoveMemberPermission(UUID teamId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.TEAM_MEMBERSHIP, Operations.REMOVE_MEMBER)) {
            throw new AccessDeniedException("User does not have permission to remove members from team");
        }
    }

    /**
     * Enforce "assign lead" permission check for a specific team
     *
     * @param teamId The team identifier
     */
    public void enforceAssignLeadPermission(UUID teamId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.TEAM_LEADERSHIP, Operations.ASSIGN_LEAD)) {
            throw new AccessDeniedException("User does not have permission to assign team leads");
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
