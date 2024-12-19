package com.projecthub.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for authentication results.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResult {

    private String token;
    private String rememberMeToken;
    private UUID userId;
}