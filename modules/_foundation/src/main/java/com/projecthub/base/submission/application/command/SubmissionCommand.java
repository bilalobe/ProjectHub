package com.projecthub.base.submission.application.command;

import java.util.UUID;

public sealed interface SubmissionCommand {
    record CreateSubmission(
        UUID studentId,
        UUID projectId,
        String content,
        String filePath,
        boolean isLate,
        UUID initiatorId
    ) implements SubmissionCommand {}

    record UpdateSubmission(
        UUID submissionId,
        String content,
        String filePath,
        UUID initiatorId
    ) implements SubmissionCommand {}

    record SubmitSubmission(
        UUID submissionId,
        UUID initiatorId
    ) implements SubmissionCommand {}

    record GradeSubmission(
        UUID submissionId,
        int grade,
        String feedback,
        UUID initiatorId
    ) implements SubmissionCommand {}

    record RevokeSubmission(
        UUID submissionId,
        String reason,
        UUID initiatorId
    ) implements SubmissionCommand {}

    record AddComment(
        UUID submissionId,
        String text,
        UUID initiatorId
    ) implements SubmissionCommand {}
}