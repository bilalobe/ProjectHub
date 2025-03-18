package com.projecthub.auth.service;

import com.projecthub.auth.api.dto.RegisterRequestDTO;

/**
 * Service responsible for handling user registration.
 */
public interface RegistrationService {
    
    /**
     * Registers a new user in the system.
     *
     * @param request The registration details
     * @return The ID of the newly registered user
     */
    String registerUser(RegisterRequestDTO request);
    
    /**
     * Checks if a username is already taken.
     *
     * @param username The username to check
     * @return true if the username is already in use, false otherwise
     */
    boolean isUsernameTaken(String username);
    
    /**
     * Checks if an email address is already registered.
     *
     * @param email The email address to check
     * @return true if the email is already in use, false otherwise
     */
    boolean isEmailTaken(String email);
}