package com.projecthub.core.dto;

import java.util.UUID;

/**
 * DTO for authentication results.
 */
public record AuthenticationResult(String token, String rememberMeToken, UUID userId) {
}