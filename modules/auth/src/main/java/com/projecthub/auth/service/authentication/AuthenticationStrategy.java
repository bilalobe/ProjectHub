package com.projecthub.auth.service.authentication;

import com.projecthub.auth.api.dto.LoginRequestDTO;

/**
 * Strategy interface for different authentication methods.
 * This interface enables pluggable authentication mechanisms.
 */
public interface AuthenticationStrategy {
    /**
     * Authenticates a user based on login request details.
     *
     * @param userId The user identifier to authenticate
     * @param request The login request containing credentials
     * @return true if authentication is successful, false otherwise
     */
    boolean authenticate(String userId, LoginRequestDTO request);

    /**
     * Determines if this strategy supports the given login request type.
     *
     * @param request The login request to check
     * @return true if this strategy can handle the request, false otherwise
     */
    boolean supports(LoginRequestDTO request);
}