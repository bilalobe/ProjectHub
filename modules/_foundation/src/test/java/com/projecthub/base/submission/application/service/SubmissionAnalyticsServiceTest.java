package com.projecthub.base.submission.application.service;

import com.projecthub.base.submission.application.port.SubmissionPort;
import com.projecthub.base.submission.application.service.SubmissionAnalyticsService.GradingTimeStats;
import com.projecthub.base.submission.application.service.SubmissionAnalyticsService.ProjectSubmissionStats;
import com.projecthub.base.submission.application.service.SubmissionAnalyticsService.StudentSubmissionStats;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.domain.enums.SubmissionStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubmissionAnalyticsService Tests")
class SubmissionAnalyticsServiceTest {

    @Mock
    private SubmissionPort submissionPort;

    @InjectMocks
    private SubmissionAnalyticsService analyticsService;

    SubmissionAnalyticsServiceTest() {
    }

    @Test
    @DisplayName("Should calculate project statistics correctly")
    void shouldCalculateProjectStatsCorrectly() {
        // given
        UUID projectId = UUID.randomUUID();
        List<Submission> submissions = createTestSubmissions(projectId);
        Mockito.when(submissionPort.findByProjectId(projectId)).thenReturn(submissions);

        // when
        ProjectSubmissionStats stats = analyticsService.getProjectStats(projectId);

        // then
        Assertions.assertThat(stats.totalSubmissions()).isEqualTo(3L);
        Assertions.assertThat(stats.gradedSubmissions()).isEqualTo(2L);
        Assertions.assertThat(stats.lateSubmissions()).isEqualTo(1L);
        Assertions.assertThat(stats.averageGrade()).isEqualTo(87.5);
        Assertions.assertThat(stats.statusDistribution())
            .containsEntry(SubmissionStatus.GRADED, Long.valueOf(2L))
            .containsEntry(SubmissionStatus.SUBMITTED, Long.valueOf(1L));
    }

    @Test
    @DisplayName("Should calculate student statistics correctly")
    void shouldCalculateStudentStatsCorrectly() {
        // given
        UUID studentId = UUID.randomUUID();
        List<Submission> submissions = createStudentSubmissions(studentId);
        Mockito.when(submissionPort.findByStudentId(studentId)).thenReturn(submissions);

        // when
        StudentSubmissionStats stats = analyticsService.getStudentStats(studentId);

        // then
        Assertions.assertThat(stats.totalSubmissions()).isEqualTo(2L);
        Assertions.assertThat(stats.onTimeSubmissions()).isEqualTo(1L);
        Assertions.assertThat(stats.lateSubmissions()).isEqualTo(1L);
        Assertions.assertThat(stats.averageGrade()).isEqualTo(85.0);
        Assertions.assertThat(stats.projectGrades()).hasSize(2);
    }

    @Test
    @DisplayName("Should calculate grading time statistics correctly")
    void shouldCalculateGradingTimeStatsCorrectly() {
        // given
        UUID projectId = UUID.randomUUID();
        List<Submission> submissions = createGradedSubmissions(projectId);
        Mockito.when(submissionPort.findByProjectId(projectId)).thenReturn(submissions);

        // when
        GradingTimeStats stats = analyticsService.getGradingTimeStats(projectId);

        // then
        Assertions.assertThat(stats.gradedSubmissionsCount()).isEqualTo(2);
        Assertions.assertThat(stats.averageGradingTime()).isGreaterThan(Duration.ZERO);
        Assertions.assertThat(stats.maxGradingTime()).isGreaterThan(stats.minGradingTime());
    }

    private static List<Submission> createTestSubmissions(UUID projectId) {
        Submission graded1 = Submission.create(UUID.randomUUID(), projectId, "content1", "file1.txt", false);
        graded1.submit();
        graded1.grade(90, "Excellent!", UUID.randomUUID());

        Submission graded2 = Submission.create(UUID.randomUUID(), projectId, "content2", "file2.txt", true);
        graded2.submit();
        graded2.grade(85, "Good work!", UUID.randomUUID());

        Submission pending = Submission.create(UUID.randomUUID(), projectId, "content3", "file3.txt", false);
        pending.submit();

        return List.of(graded1, graded2, pending);
    }

    private static List<Submission> createStudentSubmissions(UUID studentId) {
        Submission sub1 = Submission.create(studentId, UUID.randomUUID(), "content1", "file1.txt", false);
        sub1.submit();
        sub1.grade(80, "Good!", UUID.randomUUID());

        Submission sub2 = Submission.create(studentId, UUID.randomUUID(), "content2", "file2.txt", true);
        sub2.submit();
        sub2.grade(90, "Excellent!", UUID.randomUUID());

        return List.of(sub1, sub2);
    }

    private List<Submission> createGradedSubmissions(UUID projectId) {
        Submission quick = Submission.create(UUID.randomUUID(), projectId, "content1", "file1.txt", false);
        quick.submit();
        // Simulate submission time
        setSubmittedAt(quick, Instant.now().minus(2L, ChronoUnit.HOURS));
        quick.grade(85, "Good!", UUID.randomUUID());

        Submission slow = Submission.create(UUID.randomUUID(), projectId, "content2", "file2.txt", false);
        slow.submit();
        // Simulate submission time
        setSubmittedAt(slow, Instant.now().minus(24L, ChronoUnit.HOURS));
        slow.grade(90, "Excellent!", UUID.randomUUID());

        return List.of(quick, slow);
    }

    private static void setSubmittedAt(Submission submission, Instant time) {
        // Use reflection to set the submittedAt field since it's private
        try {
            var field = Submission.class.getDeclaredField("submittedAt");
            field.setAccessible(true);
            field.set(submission, time);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
