package com.projecthub.base.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for handling user login requests.
 * Contains credentials and additional login context information.
 *
 * @param principal  User's login identifier
 * @param password   User's authentication credential
 * @param rememberMe Flag indicating if the session should be persisted
 * @param ipAddress  IP address from which the login attempt originated
 */
public record LoginRequestDTO(
    @NotBlank(message = "Username/email is required")
    String principal,

    @NotBlank(message = "Password is required")
    String password,

    boolean rememberMe,
    String ipAddress
) {
}
