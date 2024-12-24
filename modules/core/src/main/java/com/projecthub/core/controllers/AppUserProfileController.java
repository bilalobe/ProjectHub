package com.projecthub.core.controllers;

import com.projecthub.core.dto.AppUserProfileDTO;
import com.projecthub.core.dto.UpdateUserRequestDTO;
import com.projecthub.core.services.user._AppUserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * REST controller for handling user profile operations. Provides endpoints for
 * retrieving and updating user profiles.
 */
@RestController
@RequestMapping("/api/v1/users/{userId}/profile")
@Tag(name = "User Profile API", description = "Operations for managing user profiles")
public class AppUserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(AppUserProfileController.class);

    private final _AppUserProfileService userProfileService;

    public AppUserProfileController(_AppUserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Retrieves a user profile by their UUID.
     *
     * @param userId the UUID of the user to retrieve
     * @return the UserProfileDTO of the retrieved user
     */
    @Operation(summary = "Get user profile")
    @GetMapping
    public ResponseEntity<AppUserProfileDTO> getUserProfile(@PathVariable UUID userId) {
        logger.info("Retrieving profile for user with ID: {}", userId);
        AppUserProfileDTO profile = userProfileService.getUserProfileById(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Updates a user profile with the provided details.
     *
     * @param userId        the UUID of the user to update
     * @param updateRequest the data transfer object containing updated profile
     *                      details
     * @return the updated UserProfileDTO
     */
    @Operation(summary = "Update user profile")
    @PutMapping
    public ResponseEntity<AppUserProfileDTO> updateUserProfile(@PathVariable UUID userId,
                                                               @Valid @RequestBody UpdateUserRequestDTO updateRequest) {
        logger.info("Updating profile for user with ID: {}", userId);
        AppUserProfileDTO updatedProfile = userProfileService.updateUserProfile(userId, updateRequest);
        logger.info("Successfully updated profile for user with ID: {}", userId);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Updates the avatar of a user profile.
     *
     * @param userId the UUID of the user to update
     * @param avatar the new avatar file
     * @return a response entity indicating the result of the operation
     */
    @Operation(summary = "Update user avatar")
    @PutMapping("/avatar")
    public ResponseEntity<Void> updateAvatar(@PathVariable UUID userId, @RequestParam("avatar") MultipartFile avatar) {
        logger.info("Updating avatar for user with ID: {}", userId);
        userProfileService.updateAvatar(userId, avatar);
        return ResponseEntity.ok().build();
    }

    /**
     * Updates the status of a user profile.
     *
     * @param userId the UUID of the user to update
     * @param status the new status
     * @return a response entity indicating the result of the operation
     */
    @Operation(summary = "Update user status")
    @PutMapping("/status")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID userId, @RequestParam("status") String status) {
        logger.info("Updating status for user with ID: {}", userId);
        userProfileService.updateStatus(userId, status);
        return ResponseEntity.ok().build();
    }
}