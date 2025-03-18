package com.projecthub.base.submission.application.service;

import com.projecthub.base.submission.application.command.SubmissionCommand;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.domain.enums.SubmissionStatus;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NonNls;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("SubmissionBatchService Tests")
class SubmissionBatchServiceTest {

    @Mock
    private SubmissionCommandService commandService;

    @Mock
    private SubmissionQueryService queryService;

    @InjectMocks
    private SubmissionBatchService batchService;

    SubmissionBatchServiceTest() {
    }

    @Test
    @DisplayName("Should batch grade submissions successfully")
    void shouldBatchGradeSubmissionsSuccessfully() throws ExecutionException, InterruptedException {
        // given
        Set<UUID> submissionIds = Set.of(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
        UUID graderId = UUID.randomUUID();
        int grade = 85;
        @NonNls String feedback = "Good work!";

        List<Submission> gradedSubmissions = submissionIds.stream()
            .map(id -> {
                Submission submission = createTestSubmission(id);
                submission.submit();
                submission.grade(grade, feedback, graderId);
                return submission;
            })
            .toList();

        Mockito.when(commandService.handleGrade(ArgumentMatchers.any(SubmissionCommand.GradeSubmission.class)))
            .thenReturn(gradedSubmissions.get(0))
            .thenReturn(gradedSubmissions.get(1))
            .thenReturn(gradedSubmissions.get(2));

        // when
        CompletableFuture<List<Submission>> future = batchService.batchGrade(
            submissionIds,
            grade,
            feedback,
            graderId
        );
        List<Submission> results = future.get();

        // then
        Assertions.assertThat(results).hasSize(3);
        Assertions.assertThat(results).allMatch(s -> s.getStatus() == SubmissionStatus.GRADED);
        Assertions.assertThat(results).allMatch(s -> s.getGrade() == grade);
        Assertions.assertThat(results).allMatch(s -> s.getFeedback().equals(feedback));
    }

    @Test
    @DisplayName("Should batch revoke submissions successfully")
    void shouldBatchRevokeSubmissionsSuccessfully() throws ExecutionException, InterruptedException {
        // given
        Set<UUID> submissionIds = Set.of(
            UUID.randomUUID(),
            UUID.randomUUID()
        );
        UUID initiatorId = UUID.randomUUID();
        String reason = "Plagiarism detected";

        List<Submission> revokedSubmissions = submissionIds.stream()
            .map(id -> {
                Submission submission = createTestSubmission(id);
                submission.submit();
                submission.revoke(reason, initiatorId);
                return submission;
            })
            .toList();

        Mockito.when(commandService.handleRevoke(ArgumentMatchers.any(SubmissionCommand.RevokeSubmission.class)))
            .thenReturn(revokedSubmissions.get(0))
            .thenReturn(revokedSubmissions.get(1));

        // when
        CompletableFuture<List<Submission>> future = batchService.batchRevoke(
            submissionIds,
            reason,
            initiatorId
        );
        List<Submission> results = future.get();

        // then
        Assertions.assertThat(results).hasSize(2);
        Assertions.assertThat(results).allMatch(s -> s.getStatus() == SubmissionStatus.REVOKED);
    }

    @Test
    @DisplayName("Should find overdue submissions correctly")
    void shouldFindOverdueSubmissionsCorrectly() throws ExecutionException, InterruptedException {
        // given
        UUID projectId = UUID.randomUUID();
        List<Submission> submissions = List.of(
            createOverdueSubmission(UUID.randomUUID()),
            createOverdueSubmission(UUID.randomUUID()),
            createOnTimeSubmission(UUID.randomUUID())
        );

        Mockito.when(queryService.findByProjectId(projectId)).thenReturn(submissions);

        // when
        CompletableFuture<List<UUID>> future = batchService.findOverdueSubmissions(projectId);
        List<UUID> overdueIds = future.get();

        // then
        Assertions.assertThat(overdueIds).hasSize(2);
    }

    @Test
    @DisplayName("Should validate batch operation size")
    void shouldValidateBatchOperationSize() {
        // given
        Set<UUID> tooManyIds = generateLargeSubmissionIdSet(101);

        // when/then
        Assertions.assertThatThrownBy(() -> batchService.validateBatchOperation(tooManyIds))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Cannot process more than 100 submissions");
    }

    private static Submission createTestSubmission(UUID id) {
        return Submission.create(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Test content",
            "test.txt",
            false
        );
    }

    private Submission createOverdueSubmission(UUID id) {
        Submission submission = createTestSubmission(id);
        submission.submit();
        return submission;
    }

    private Submission createOnTimeSubmission(UUID id) {
        return createTestSubmission(id);
    }

    private static Set<UUID> generateLargeSubmissionIdSet(int size) {
        return java.util.stream.Stream.generate(UUID::randomUUID)
            .limit((long) size)
            .collect(java.util.stream.Collectors.toSet());
    }
}
