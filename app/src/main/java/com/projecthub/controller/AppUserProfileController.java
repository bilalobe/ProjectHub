package com.projecthub.controller;

import com.projecthub.dto.AppUserProfileDTO;
import com.projecthub.service.AppUserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for handling user profile operations.
 * Provides endpoints for retrieving and updating user profiles.
 */
@RestController
@RequestMapping("/profile")
public class AppUserProfileController {

    private final AppUserProfileService userProfileService;

    @Autowired
    public AppUserProfileController(AppUserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Retrieves a user profile by their UUID.
     *
     * @param id the UUID of the user to retrieve
     * @return the UserProfileDTO of the retrieved user
     */
    @GetMapping("/{id}")
    public AppUserProfileDTO getUserProfile(@PathVariable UUID id) {
        return userProfileService.getUserProfileById(id);
    }

    /**
     * Updates a user profile with the provided details.
     *
     * @param id the UUID of the user to update
     * @param userProfileDTO the data transfer object containing updated profile details
     * @return the updated UserProfileDTO
     */
    @PutMapping("/{id}")
    public AppUserProfileDTO updateUserProfile(@PathVariable UUID id, @RequestBody AppUserProfileDTO userProfileDTO) {
        return userProfileService.updateUserProfile(id, userProfileDTO);
    }
}