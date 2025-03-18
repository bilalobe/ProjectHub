package com.projecthub.base.submission.domain.event;

import java.time.Instant;
import java.util.UUID;

public sealed interface SubmissionDomainEvent {
    UUID eventId();
    UUID submissionId();
    Instant timestamp();

    record Created(
        UUID eventId,
        UUID submissionId,
        UUID studentId,
        UUID projectId,
        String content,
        String filePath,
        boolean isLate,
        Instant timestamp
    ) implements SubmissionDomainEvent {}

    record Updated(
        UUID eventId,
        UUID submissionId,
        String content,
        String filePath,
        Instant timestamp
    ) implements SubmissionDomainEvent {}

    record Submitted(
        UUID eventId,
        UUID submissionId,
        UUID studentId,
        UUID projectId,
        Instant submittedAt,
        boolean isLate,
        Instant timestamp
    ) implements SubmissionDomainEvent {}

    record Graded(
        UUID eventId,
        UUID submissionId,
        UUID graderId,
        int grade,
        String feedback,
        Instant timestamp
    ) implements SubmissionDomainEvent {}

    record Revoked(
        UUID eventId,
        UUID submissionId,
        UUID initiatorId,
        String reason,
        Instant timestamp
    ) implements SubmissionDomainEvent {}

    record CommentAdded(
        UUID eventId,
        UUID submissionId,
        UUID authorId,
        String comment,
        Instant timestamp
    ) implements SubmissionDomainEvent {}
}
