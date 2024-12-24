package com.projecthub.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for authentication token responses.
 * 
 * <p>Contains JWT access token and refresh token
 * after successful authentication.</p>
 */
public record TokenResponseDTO(
    @NotBlank(message = "Access token is required")
    String accessToken,
    
    @NotBlank(message = "Refresh token is required") 
    String refreshToken,
    
    @NotNull(message = "Token expiry is required")
    Long expiresIn
) {}

