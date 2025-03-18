package com.projecthub.auth.api;

import com.projecthub.auth.api.dto.AuthenticationResultDTO;
import com.projecthub.auth.api.dto.LoginRequestDTO;

/**
 * Core authentication interface that defines the contract for authentication operations.
 * This is the primary entry point for authentication services across modules.
 */
public interface AuthenticationFacade {
    /**
     * Authenticate a user with provided credentials.
     *
     * @param loginRequest The login credentials and context
     * @return Authentication result containing token and user info
     */
    AuthenticationResultDTO authenticate(LoginRequestDTO loginRequest);
    
    /**
     * Validate an authentication token.
     *
     * @param token The token to validate
     * @return true if token is valid, false otherwise
     */
    boolean validateToken(String token);
    
    /**
     * Invalidate all sessions for a user.
     *
     * @param userId The ID of the user
     */
    void invalidateAllSessions(String userId);
    
    /**
     * Log out a user by invalidating their current session.
     *
     * @param username The username to log out
     */
    void logout(String username);
}