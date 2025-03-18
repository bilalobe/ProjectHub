package com.projecthub.base.submission.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GradeSubmissionRequest(
    @Min(value = 0L, message = "Grade cannot be less than 0")
    @Max(value = 100L, message = "Grade cannot be more than 100")
    int grade,

    @NotBlank(message = "Feedback is required")
    @Size(max = 1000, message = "Feedback cannot exceed 1000 characters")
    String feedback
) {}
