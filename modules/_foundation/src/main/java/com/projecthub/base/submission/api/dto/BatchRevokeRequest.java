package com.projecthub.base.submission.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record BatchRevokeRequest(
    @NotEmpty(message = "Submission IDs cannot be empty")
    @Size(max = 100, message = "Cannot revoke more than 100 submissions at once")
    Set<UUID> submissionIds,

    @NotBlank(message = "Revocation reason is required")
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    String reason
) {}
