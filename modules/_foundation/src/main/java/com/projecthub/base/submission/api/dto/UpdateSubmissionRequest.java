package com.projecthub.base.submission.api.dto;

import jakarta.validation.constraints.Size;

public record UpdateSubmissionRequest(
    @Size(max = 50000, message = "Content cannot exceed 50000 characters")
    String content,
    
    @Size(max = 500, message = "File path cannot exceed 500 characters")
    String filePath
) {}