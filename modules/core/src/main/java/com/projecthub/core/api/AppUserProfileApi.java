package com.projecthub.core.api;

import com.projecthub.core.dto.AppUserProfileDTO;
import com.projecthub.core.dto.UpdateUserRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import java.util.UUID;

@Tag(name = "User Profile API", description = "Operations for managing user profiles")
public interface AppUserProfileApi {

    @Operation(summary = "Get user profile")
    ResponseEntity<AppUserProfileDTO> getUserProfile(
        @Parameter(description = "User ID") UUID userId);

    @Operation(summary = "Update user profile")
    ResponseEntity<AppUserProfileDTO> updateUserProfile(
        @Parameter(description = "User ID") UUID userId,
        @Parameter(description = "Updated profile") @Valid UpdateUserRequestDTO updateRequest);
}