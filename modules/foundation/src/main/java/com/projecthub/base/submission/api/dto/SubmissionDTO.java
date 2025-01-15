package com.projecthub.base.submission.api.dto;

import com.projecthub.base.shared.domain.enums.sync.SubmissionStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

public record SubmissionDTO(
    @NotNull UUID id,
    @NotBlank @Size(max = 5000) String content,
    @NotNull @PastOrPresent LocalDateTime timestamp,
    @Min(0) @Max(100) Integer grade,
    @Size(max = 1000) String feedback,
    boolean isLate,
    @Size(max = 500) String reviewerNotes,
    @NotNull SubmissionStatus status,
    @Size(max = 255) @Pattern(regexp = "^[a-zA-Z]:\\\\(?:[^\\\\/:*?\"<>|\\r\\n]+\\\\)*[^\\\\/:*?\"<>|\\r\\n]*$") String filePath,
    @NotNull UUID studentId,
    @NotNull UUID projectId,
    @NotBlank String submissionNumber
) {}