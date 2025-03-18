package com.projecthub.base.submission.application.service;

import com.projecthub.base.submission.application.command.SubmissionCommand;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.domain.enums.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionBatchService {
    private final SubmissionCommandService commandService;
    private final SubmissionQueryService queryService;

    @Async
    @Transactional
    public CompletableFuture<List<Submission>> batchGrade(Collection<UUID> submissionIds, int grade, String feedback, UUID graderId) {
        log.info("Batch grading {} submissions", Integer.valueOf(submissionIds.size()));

        return CompletableFuture.supplyAsync(() ->
            submissionIds.parallelStream()
                .map(id -> {
                    try {
                        SubmissionCommand.GradeSubmission command = new SubmissionCommand.GradeSubmission(
                            id, grade, feedback, graderId
                        );
                        return commandService.handleGrade(command);
                    } catch (RuntimeException e) {
                        log.error("Failed to grade submission {}: {}", id, e.getMessage());
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .toList()
        );
    }

    @Async
    @Transactional
    public CompletableFuture<List<Submission>> batchRevoke(Collection<UUID> submissionIds, String reason, UUID initiatorId) {
        log.info("Batch revoking {} submissions", Integer.valueOf(submissionIds.size()));

        return CompletableFuture.supplyAsync(() ->
            submissionIds.parallelStream()
                .map(id -> {
                    try {
                        SubmissionCommand.RevokeSubmission command = new SubmissionCommand.RevokeSubmission(
                            id, reason, initiatorId
                        );
                        return commandService.handleRevoke(command);
                    } catch (RuntimeException e) {
                        log.error("Failed to revoke submission {}: {}", id, e.getMessage());
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .toList()
        );
    }

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<List<UUID>> findOverdueSubmissions(UUID projectId) {
        log.info("Finding overdue submissions for project {}", projectId);

        return CompletableFuture.supplyAsync(() ->
            queryService.findByProjectId(projectId).stream()
                .filter(s -> s.getStatus() == SubmissionStatus.SUBMITTED && s.isLate())
                .map(Submission::getSubmissionId)
                .toList()
        );
    }

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<List<UUID>> findUngraded(UUID projectId) {
        log.info("Finding ungraded submissions for project {}", projectId);

        return CompletableFuture.supplyAsync(() ->
            queryService.findPendingGradingByProjectId(projectId).stream()
                .map(Submission::getSubmissionId)
                .toList()
        );
    }

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<Long> countByStatus(SubmissionStatus status, UUID projectId) {
        log.info("Counting submissions with status {} for project {}", status, projectId);

        return CompletableFuture.supplyAsync(() ->
                Long.valueOf(queryService.findByProjectId(projectId).stream()
                        .filter(s -> s.getStatus() == status)
                        .count())
        );
    }

    public void validateBatchOperation(Collection<UUID> submissionIds) {
        if (submissionIds == null || submissionIds.isEmpty()) {
            throw new IllegalArgumentException("Submission IDs cannot be null or empty");
        }
        if (submissionIds.size() > 100) {
            throw new IllegalArgumentException("Cannot process more than 100 submissions at once");
        }
    }
}
