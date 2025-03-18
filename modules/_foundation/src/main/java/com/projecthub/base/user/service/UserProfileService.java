package com.projecthub.base.user.service;

import com.projecthub.base.user.domain.entity.AppUser;
import com.projecthub.base.user.api.dto.UserProfileDTO;

import java.util.Optional;

/**
 * Service for managing application-specific user profile data.
 * This keeps user profile management separate from authentication concerns.
 */
public interface UserProfileService {
    
    /**
     * Find a user by their unique identifier.
     *
     * @param userId The user's ID
     * @return Optional containing the user if found
     */
    Optional<AppUser> findById(String userId);
    
    /**
     * Find a user by username.
     *
     * @param username The username to search for
     * @return Optional containing the user if found
     */
    Optional<AppUser> findByUsername(String username);
    
    /**
     * Find a user by email address.
     *
     * @param email The email address to search for
     * @return Optional containing the user if found
     */
    Optional<AppUser> findByEmail(String email);
    
    /**
     * Update a user's profile information.
     *
     * @param userId The ID of the user to update
     * @param profileDTO DTO containing the profile data to update
     * @return The updated user
     */
    AppUser updateProfile(String userId, UserProfileDTO profileDTO);
    
    /**
     * Create a new user profile from registration data.
     *
     * @param username The username for the new user
     * @param email The email for the new user
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @return The newly created user
     */
    AppUser createUserProfile(String username, String email, String firstName, String lastName);
}