package com.projecthub.base.submission.api.dto;

import jakarta.validation.constraints.*;

import java.util.Set;
import java.util.UUID;

public record BatchGradeRequest(
    @NotEmpty(message = "Submission IDs cannot be empty")
    @Size(max = 100, message = "Cannot grade more than 100 submissions at once")
    Set<UUID> submissionIds,

    @Min(value = 0L, message = "Grade cannot be less than 0")
    @Max(value = 100L, message = "Grade cannot be more than 100")
    int grade,

    @NotBlank(message = "Feedback is required")
    @Size(max = 1000, message = "Feedback cannot exceed 1000 characters")
    String feedback
) {}
