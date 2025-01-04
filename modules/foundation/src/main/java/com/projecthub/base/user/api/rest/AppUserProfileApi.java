package com.projecthub.base.user.api.rest;

import com.projecthub.base.user.api.dto.AppUserProfileDTO;
import com.projecthub.base.user.api.dto.UpdateUserRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "User Profile API", description = "Operations for managing user profiles")
@RequestMapping("/api/v1/users/{userId}/profile")
public interface AppUserProfileApi {

    HttpStatus NO_CONTENT = null;

    @GetMapping
    @Operation(summary = "Get user profile")
    ResponseEntity<AppUserProfileDTO> getUserProfile(
        @PathVariable @Parameter(description = "User ID") UUID userId);

    @PutMapping
    @Operation(summary = "Update user profile")
    ResponseEntity<AppUserProfileDTO> updateUserProfile(
        @PathVariable @Parameter(description = "User ID") UUID userId,
        @RequestBody @Valid @Parameter(description = "Updated profile") UpdateUserRequestDTO updateRequest);

    @PutMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update user avatar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateAvatar(
        @PathVariable @Parameter(description = "User ID") UUID userId,
        @RequestParam("avatar") MultipartFile avatar);

    @PutMapping("/status")
    @Operation(summary = "Update user status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateStatus(
        @PathVariable @Parameter(description = "User ID") UUID userId,
        @RequestParam("status") String status);
}
