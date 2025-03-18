package com.projecthub.auth.service;

import com.projecthub.auth.api.dto.LoginRequestDTO;
import com.projecthub.auth.dto.AuthenticationResponse;

/**
 * Core authentication service for handling user login attempts.
 */
public interface AuthenticationService {
    /**
     * Authenticates a user based on provided credentials.
     *
     * @param request Login request containing authentication details
     * @return Authentication response with token and user information
     * @throws com.projecthub.auth.exception.AuthenticationException if authentication fails
     */
    AuthenticationResponse authenticate(LoginRequestDTO request);
    
    /**
     * Validates an authentication token.
     *
     * @param token The JWT or other token to validate
     * @return true if token is valid, false otherwise
     */
    boolean validateToken(String token);
    
    /**
     * Logs out a user by invalidating their authentication token.
     *
     * @param token The authentication token to invalidate
     */
    void logout(String token);
}