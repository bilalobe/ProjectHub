package com.projecthub.core.dto;

import jakarta.validation.constraints.*;

/**
 * DTO for user registration requests.
 * 
 * <p>Validation rules:
 * <ul>
 *   <li>Username: 3-50 chars, alphanumeric with dots/underscores/hyphens</li>
 *   <li>Email: Valid email format</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>
 * var request = new RegisterRequestDTO(
 *     new AppUserCredentialsDTO("username", "Pass123!"),
 *     "user@example.com",
 *     "johndoe"
 * );
 * </pre>
 */
public record RegisterRequestDTO(
    AppUserCredentialsDTO credentials,

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    String email,

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", 
            message = "Username can only contain letters, numbers, dots, underscores and hyphens")
    String username
) {
    /**
     * Validates the record's fields at construction time.
     * @throws IllegalArgumentException if any field is invalid
     */
    public RegisterRequestDTO {
        if (credentials == null || email == null || username == null) {
            throw new IllegalArgumentException("All fields must be non-null");
        }
    }
}