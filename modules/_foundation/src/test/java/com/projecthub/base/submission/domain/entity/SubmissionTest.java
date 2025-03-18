package com.projecthub.base.submission.domain.entity;

import com.projecthub.base.submission.domain.enums.SubmissionStatus;
import com.projecthub.base.submission.domain.event.SubmissionDomainEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@DisplayName("Submission Entity Tests")
class SubmissionTest {

    SubmissionTest() {
    }

    @Nested
    @DisplayName("Creation Tests")
    class CreationTests {
        CreationTests() {
        }

        @Test
        @DisplayName("Should create submission with valid data")
        void shouldCreateSubmissionWithValidData() {
            // given
            UUID studentId = UUID.randomUUID();
            UUID projectId = UUID.randomUUID();
            String content = "Test content";
            String filePath = "test.txt";

            // when
            Submission submission = Submission.create(studentId, projectId, content, filePath, false);

            // then
            Assertions.assertThat(submission).isNotNull();
            assertThat(submission.getSubmissionId()).isNotNull();
            Assertions.assertThat(submission.getStatus()).isEqualTo(SubmissionStatus.DRAFT);
            Assertions.assertThat(submission.getDomainEvents())
                .hasSize(1)
                .hasOnlyElementsOfType(SubmissionDomainEvent.Created.class);
        }
    }

    @Nested
    @DisplayName("Submission Workflow Tests")
    class WorkflowTests {
        WorkflowTests() {
        }

        @Test
        @DisplayName("Should transition from draft to submitted")
        void shouldTransitionFromDraftToSubmitted() {
            // given
            Submission submission = createTestSubmission();

            // when
            submission.submit();

            // then
            Assertions.assertThat(submission.getStatus()).isEqualTo(SubmissionStatus.SUBMITTED);
            assertThat(submission.getSubmittedAt()).isNotNull();
            Assertions.assertThat(submission.getDomainEvents())
                .hasSize(2)
                .anySatisfy(event -> assertThat(event)
                    .isInstanceOf(SubmissionDomainEvent.Submitted.class));
        }

        @Test
        @DisplayName("Should not allow submitting already submitted submission")
        void shouldNotAllowSubmittingAlreadySubmittedSubmission() {
            // given
            Submission submission = createTestSubmission();
            submission.submit();

            // when/then
            Assertions.assertThatThrownBy(() -> submission.submit())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Can only submit draft submissions");
        }

        @Test
        @DisplayName("Should allow grading submitted submission")
        void shouldAllowGradingSubmittedSubmission() {
            // given
            Submission submission = createTestSubmission();
            submission.submit();
            UUID graderId = UUID.randomUUID();

            // when
            submission.grade(85, "Good work!", graderId);

            // then
            Assertions.assertThat(submission.getStatus()).isEqualTo(SubmissionStatus.GRADED);
            Assertions.assertThat(submission.getGrade()).isEqualTo(85);
            Assertions.assertThat(submission.getFeedback()).isEqualTo("Good work!");
            Assertions.assertThat(submission.getDomainEvents())
                .hasSize(3)
                .anySatisfy(event -> assertThat(event)
                    .isInstanceOf(SubmissionDomainEvent.Graded.class));
        }
    }

    @Nested
    @DisplayName("Comment Tests")
    class CommentTests {
        CommentTests() {
        }

        @Test
        @DisplayName("Should add comment to submission")
        void shouldAddCommentToSubmission() {
            // given
            Submission submission = createTestSubmission();
            UUID authorId = UUID.randomUUID();
            String commentText = "Great work!";

            // when
            submission.addComment(commentText, authorId);

            // then
            assertThat(submission.getComments()).hasSize(1);
            Assertions.assertThat(submission.getDomainEvents())
                .hasSize(2)
                .anySatisfy(event -> assertThat(event)
                    .isInstanceOf(SubmissionDomainEvent.CommentAdded.class));
        }
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
