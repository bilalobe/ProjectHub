package com.projecthub.base.submission.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateSubmissionRequest(
    @NotNull(message = "Student ID is required")
    UUID studentId,

    @NotNull(message = "Project ID is required")
    UUID projectId,

    @Size(max = 50000, message = "Content cannot exceed 50000 characters")
    String content,

    @Size(max = 500, message = "File path cannot exceed 500 characters")
    String filePath,

    boolean isLate
) {}
