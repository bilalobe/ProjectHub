package com.projecthub.base.auth.service.fortress;

import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.model.Permission;
import org.apache.directory.fortress.core.model.Session;
import org.apache.directory.fortress.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service for checking user permissions using Apache Fortress.
 * This service implements access control decisions based on RBAC.
 */
@Service
public class FortressAccessControlService {

    private static final Logger log = LoggerFactory.getLogger(FortressAccessControlService.class);
    private final AccessMgr accessManager;

    public FortressAccessControlService(AccessMgr accessManager) {
        this.accessManager = accessManager;
    }

    /**
     * Check if the current authenticated user has a specific permission.
     *
     * @param objectName the protected resource name
     * @param operation the operation to be performed
     * @return true if the user has the permission, false otherwise
     */
    public boolean hasPermission(String objectName, String operation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No authenticated user found when checking permission {}:{}", objectName, operation);
            return false;
        }

        return hasPermission(authentication, objectName, operation);
    }

    /**
     * Check if the authenticated user has a specific permission.
     *
     * @param authentication the authentication object
     * @param objectName the protected resource name
     * @param operation the operation to be performed
     * @return true if the user has the permission, false otherwise
     */
    public boolean hasPermission(Authentication authentication, String objectName, String operation) {
        try {
            // Check if we have a Fortress session in the authentication details
            Object details = authentication.getDetails();

            if (details instanceof Session) {
                // Use existing session for permission check
                Session session = (Session) details;
                Permission permission = new Permission(objectName, operation);
                return accessManager.checkAccess(session, permission);
            } else {
                // No session available, create a new one for permission check
                String username = authentication.getName();
                log.debug("Creating new Fortress session for permission check: user={}, permission={}:{}",
                          username, objectName, operation);

                User user = new User(username);
                Session session = accessManager.createSession(user, true);
                Permission permission = new Permission(objectName, operation);
                return accessManager.checkAccess(session, permission);
            }
        } catch (RuntimeException e) {
            log.error("Error checking permission {}:{} - {}", objectName, operation, e.getMessage());
            return false;
        }
    }

    /**
     * Check if the current authenticated user has the specified role.
     *
     * @param roleName the role name to check
     * @return true if the user has the role, false otherwise
     */
    public static boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName.toUpperCase(Locale.ROOT)));
    }
}
