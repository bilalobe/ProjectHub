package com.projecthub.base.submission.application.service;

import com.projecthub.base.submission.application.command.SubmissionCommand;
import com.projecthub.base.submission.application.port.SubmissionPort;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.domain.enums.SubmissionStatus;
import com.projecthub.base.submission.infrastructure.event.adapter.SubmissionEventAdapter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubmissionCommandService Tests")
class SubmissionCommandServiceTest {

    @Mock
    private SubmissionPort submissionPort;

    @Mock
    private SubmissionEventAdapter eventAdapter;

    @InjectMocks
    private SubmissionCommandService commandService;

    SubmissionCommandServiceTest() {
    }

    @Test
    @DisplayName("Should create submission successfully")
    void shouldCreateSubmissionSuccessfully() {
        // given
        UUID studentId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID initiatorId = UUID.randomUUID();

        SubmissionCommand.CreateSubmission command = new SubmissionCommand.CreateSubmission(
            studentId,
            projectId,
            "Test content",
            "test.txt",
            false,
            initiatorId
        );

        Mockito.when(submissionPort.save(ArgumentMatchers.any(Submission.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Submission result = commandService.handleCreate(command);

        // then
        Assertions.assertThat(result).isNotNull();
        assertThat(result.getStudentId()).isEqualTo(studentId);
        assertThat(result.getProjectId()).isEqualTo(projectId);
        Assertions.assertThat(result.getStatus()).isEqualTo(SubmissionStatus.DRAFT);

        Mockito.verify(submissionPort).save(ArgumentMatchers.any(Submission.class));
        Mockito.verify(eventAdapter).publish(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Should grade submission successfully")
    void shouldGradeSubmissionSuccessfully() {
        // given
        UUID submissionId = UUID.randomUUID();
        UUID graderId = UUID.randomUUID();
        Submission submission = createTestSubmission();
        submission.submit();

        SubmissionCommand.GradeSubmission command = new SubmissionCommand.GradeSubmission(
            submissionId,
            85,
            "Good work!",
            graderId
        );

        Mockito.when(submissionPort.findById(submissionId)).thenReturn(Optional.of(submission));
        Mockito.when(submissionPort.save(ArgumentMatchers.any(Submission.class))).thenReturn(submission);

        // when
        Submission result = commandService.handleGrade(command);

        // then
        Assertions.assertThat(result.getStatus()).isEqualTo(SubmissionStatus.GRADED);
        Assertions.assertThat(result.getGrade()).isEqualTo(85);
        Assertions.assertThat(result.getFeedback()).isEqualTo("Good work!");

        Mockito.verify(submissionPort).save(ArgumentMatchers.any(Submission.class));
        Mockito.verify(eventAdapter).publish(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Should throw exception when grading non-existent submission")
    void shouldThrowExceptionWhenGradingNonExistentSubmission() {
        // given
        UUID submissionId = UUID.randomUUID();
        SubmissionCommand.GradeSubmission command = new SubmissionCommand.GradeSubmission(
            submissionId,
            85,
            "Good work!",
            UUID.randomUUID()
        );

        Mockito.when(submissionPort.findById(submissionId)).thenReturn(Optional.empty());

        // when/then
        Assertions.assertThatThrownBy(() -> commandService.handleGrade(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Submission not found");

        Mockito.verify(submissionPort, Mockito.never()).save(ArgumentMatchers.any());
        Mockito.verify(eventAdapter, Mockito.never()).publish(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Should submit submission successfully")
    void shouldSubmitSubmissionSuccessfully() {
        // given
        UUID submissionId = UUID.randomUUID();
        UUID initiatorId = UUID.randomUUID();
        Submission submission = createTestSubmission();

        SubmissionCommand.SubmitSubmission command = new SubmissionCommand.SubmitSubmission(
            submissionId,
            initiatorId
        );

        Mockito.when(submissionPort.findById(submissionId)).thenReturn(Optional.of(submission));
        Mockito.when(submissionPort.save(ArgumentMatchers.any(Submission.class))).thenReturn(submission);

        // when
        Submission result = commandService.handleSubmit(command);

        // then
        Assertions.assertThat(result.getStatus()).isEqualTo(SubmissionStatus.SUBMITTED);
        assertThat(result.getSubmittedAt()).isNotNull();

        Mockito.verify(submissionPort).save(ArgumentMatchers.any(Submission.class));
        Mockito.verify(eventAdapter).publish(ArgumentMatchers.any());
    }

    private static Submission createTestSubmission() {
        return Submission.create(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Test content",
            "test.txt",
            false
        );
    }
}
