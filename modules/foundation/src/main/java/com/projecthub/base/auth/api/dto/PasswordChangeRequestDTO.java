package com.projecthub.base.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for password change requests.
 *
 * @param oldPassword the user's old password
 * @param newPassword the user's new password
 */
public record PasswordChangeRequestDTO(
    @NotBlank(message = "Old password is mandatory") String oldPassword,
    @NotBlank(message = "New password is mandatory") String newPassword
) {
}
