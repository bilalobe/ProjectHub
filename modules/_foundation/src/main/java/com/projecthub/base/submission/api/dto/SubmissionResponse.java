package com.projecthub.base.submission.api.dto;

import com.projecthub.base.submission.domain.enums.SubmissionStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SubmissionResponse(
    UUID submissionId,
    UUID studentId,
    UUID projectId,
    String content,
    String filePath,
    Integer grade,
    String feedback,
    SubmissionStatus status,
    boolean isLate,
    Instant submittedAt,
    List<CommentResponse> comments,
    Instant createdAt,
    Instant lastModifiedAt
) {
    public record CommentResponse(
        UUID commentId,
        String text,
        UUID authorId,
        Instant createdAt
    ) {}
}
