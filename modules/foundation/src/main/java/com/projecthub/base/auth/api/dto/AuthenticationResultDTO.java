package com.projecthub.base.auth.api.dto;

import java.util.UUID;

/**
 * DTO for authentication results.
 */
public record AuthenticationResultDTO(String token, String rememberMeToken, UUID userId) {
}
