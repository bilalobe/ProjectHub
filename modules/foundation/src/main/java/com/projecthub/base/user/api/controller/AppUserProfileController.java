package com.projecthub.base.user.api.controller;

import com.projecthub.base.user.api.dto.AppUserProfileDTO;
import com.projecthub.base.user.api.dto.UpdateUserRequestDTO;
import com.projecthub.base.user.application.profile.service.AppUserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "User Profile API", description = "Operations for managing user profiles")
public class AppUserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(AppUserProfileController.class);
    private final AppUserProfileService profileService;

    public AppUserProfileController(AppUserProfileService profileService) {
        this.profileService = profileService;
    }

    @Operation(summary = "Get user profile")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    @GetMapping
    public ResponseEntity<AppUserProfileDTO> getUserProfile(@RequestHeader("X-User-Id") UUID userId) {
        logger.info("Retrieving profile for user with ID: {}", userId);
        return ResponseEntity.ok(profileService.getUserProfileById(userId));
    }

    @Operation(summary = "Update user profile")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @PutMapping
    public ResponseEntity<AppUserProfileDTO> updateUserProfile(
        @RequestHeader("X-User-Id") UUID userId,
        @Valid @RequestBody UpdateUserRequestDTO updateRequest) {
        logger.info("Updating profile for user with ID: {}", userId);
        return ResponseEntity.ok(profileService.updateUserProfile(userId, updateRequest));
    }

    @Operation(summary = "Update user avatar")
    @ApiResponse(responseCode = "204", description = "Avatar updated successfully")
    @PutMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAvatar(
        @RequestHeader("X-User-Id") UUID userId,
        @RequestParam("avatar") MultipartFile avatar) {
        logger.info("Updating avatar for user with ID: {}", userId);
        profileService.updateAvatar(userId, avatar);
    }

    @Operation(summary = "Update user status")
    @ApiResponse(responseCode = "204", description = "Status updated successfully")
    @PutMapping("/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(
        @RequestHeader("X-User-Id") UUID userId,
        @RequestParam("status") String status) {
        logger.info("Updating status for user with ID: {}", userId);
        profileService.updateStatus(userId, status);
    }
}
