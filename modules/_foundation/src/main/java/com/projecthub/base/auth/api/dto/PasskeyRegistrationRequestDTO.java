package com.projecthub.base.auth.api.dto;

/**
 * DTO for completing passkey registration.
 * Contains the client response and the original request options.
 */
public record PasskeyRegistrationRequestDTO(
    String credentialResponse,
    String requestOptions
) {}