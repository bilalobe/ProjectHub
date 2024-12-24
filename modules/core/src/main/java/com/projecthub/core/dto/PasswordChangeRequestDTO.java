package com.projecthub.core.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequestDTO(
    @NotBlank(message = "Old password is mandatory") String oldPassword,
    @NotBlank(message = "New password is mandatory") String newPassword
) {}