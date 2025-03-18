package com.projecthub.base.auth.api.dto;

/**
 * DTO for completing passkey authentication.
 * Contains the client assertion response and the original request options.
 */
public record PasskeyAuthenticationRequestDTO(
    String assertionResponse,
    String requestOptions
) {}