package com.projecthub.base.auth.service.fortress;

import com.projecthub.base.auth.api.dto.AuthenticationResultDTO;
import com.projecthub.base.auth.api.dto.LoginRequestDTO;
import com.projecthub.base.auth.application.security.SecurityAuditService;
import com.projecthub.base.auth.service.security.TokenManagementService;
import com.projecthub.base.shared.domain.enums.security.SecurityAuditAction;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.model.Session;
import org.apache.directory.fortress.core.model.User;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Authentication manager implementation using Apache Fortress.
 * This service handles the authentication flow including audit logging.
 */
@Service
public class FortressAuthenticationManager {

    private static final Logger log = LoggerFactory.getLogger(FortressAuthenticationManager.class);

    private final AccessMgr accessManager;
    private final TokenManagementService tokenService;
    @NonNls
    private final SecurityAuditService auditService;

    public FortressAuthenticationManager(
            AccessMgr accessManager,
            TokenManagementService tokenService,
            SecurityAuditService auditService) {
        this.accessManager = accessManager;
        this.tokenService = tokenService;
        this.auditService = auditService;
    }

    /**
     * Authenticates a user with Apache Fortress and generates authentication tokens.
     *
     * @param loginRequest the login request containing credentials
     * @return authentication result with tokens
     */
    @Transactional
    public AuthenticationResultDTO authenticate(LoginRequestDTO loginRequest) {
        String username = loginRequest.principal();
        String password = loginRequest.password();
        String ipAddress = loginRequest.ipAddress();

        log.info("Authentication attempt for user: {}", username);

        try {
            // Create a Fortress session (performs authentication)
            User fortressUser = new User(username, password);
            Session session = accessManager.createSession(fortressUser, false);

            // If we got here, authentication succeeded

            // Generate JWT token
            String accessToken = TokenManagementService.generateAccessToken(getFortressUserId(session));

            // Log successful authentication
            auditService.logAuthenticationAttempt(username, true, ipAddress);

            // Return authentication result with token
            return new AuthenticationResultDTO(accessToken, null, getFortressUserId(session));

        } catch (RuntimeException e) {
            // Log failed authentication
            auditService.logAuthenticationAttempt(username, false, ipAddress);
            auditService.logAccountAction(
                null,
                SecurityAuditAction.ACCESS_DENIED,
                "Failed login attempt: " + e.getMessage(),
                ipAddress
            );

            log.error("Authentication failed for user {}: {}", username, e.getMessage());
            throw new com.projecthub.base.auth.domain.exception.AuthenticationException("Invalid credentials");
        }
    }

    /**
     * Extract a UUID from the Fortress session.
     *
     * @param session the Fortress session
     * @return UUID representing the user
     */
    private static UUID getFortressUserId(Session session) {
        // Use the Fortress user ID if available, otherwise generate one
        String userId = session.getUser().getUserId();
        if (userId == null || userId.isEmpty()) {
            userId = UUID.randomUUID().toString();
            log.warn("Fortress user has no ID, generating random ID: {}", userId);
        }
        return UUID.fromString(userId);
    }
}
