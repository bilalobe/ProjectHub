package com.projecthub.base.auth.api.dto;

/**
 * DTO for responding to a passkey authentication start request.
 * Contains the assertion options for the client.
 */
public record PasskeyAuthenticationResponseDTO(
    String options,
    boolean success,
    String message
) {}