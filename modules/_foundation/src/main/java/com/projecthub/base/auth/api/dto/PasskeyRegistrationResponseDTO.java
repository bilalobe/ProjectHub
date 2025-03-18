package com.projecthub.base.auth.api.dto;

/**
 * DTO for responding to a passkey registration start request.
 * Contains the credential creation options for the client.
 */
public record PasskeyRegistrationResponseDTO(
    String options,
    boolean success,
    String message
) {}