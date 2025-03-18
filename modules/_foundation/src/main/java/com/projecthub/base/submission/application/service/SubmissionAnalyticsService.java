package com.projecthub.base.submission.application.service;

import com.projecthub.base.submission.application.port.SubmissionPort;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.domain.enums.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionAnalyticsService {
    private final SubmissionPort submissionPort;

    @Cacheable(cacheNames = "submission-stats", key = "'project-' + #projectId")
    @Transactional(readOnly = true)
    public ProjectSubmissionStats getProjectStats(UUID projectId) {
        List<Submission> submissions = submissionPort.findByProjectId(projectId);

        long totalCount = (long) submissions.size();
        long gradedCount = submissions.stream()
            .filter(s -> s.getStatus() == SubmissionStatus.GRADED)
            .count();
        long lateCount = submissions.stream()
            .filter(Submission::isLate)
            .count();

        double averageGrade = submissions == null ? 0.0 : submissions.stream()
            .filter(Objects::nonNull)
            .filter(s -> s.getGrade() != null)
            .mapToInt(Submission::getGrade)
            .average()
            .orElse(0.0);

        Map<SubmissionStatus, Long> statusDistribution = submissions.stream()
            .collect(Collectors.groupingBy(
                Submission::getStatus,
                Collectors.counting()
            ));

        return new ProjectSubmissionStats(
            projectId,
            totalCount,
            gradedCount,
            lateCount,
            averageGrade,
            statusDistribution
        );
    }

    @Cacheable(cacheNames = "submission-stats", key = "'student-' + #studentId")
    @Transactional(readOnly = true)
    public StudentSubmissionStats getStudentStats(UUID studentId) {
        List<Submission> submissions = submissionPort.findByStudentId(studentId);

        double averageGrade = submissions.stream()
            .filter(s -> s.getGrade() != null)
            .mapToInt(Submission::getGrade)
            .average()
            .orElse(0.0);

        long onTimeCount = submissions.stream()
            .filter(s -> !s.isLate())
            .count();

        long lateCount = (long) submissions.size() - onTimeCount;

        Map<UUID, Integer> projectGrades = submissions.stream()
            .filter(s -> s.getGrade() != null)
            .collect(Collectors.toMap(
                Submission::getProjectId,
                Submission::getGrade
            ));

        return new StudentSubmissionStats(
            studentId,
                (long) submissions.size(),
            averageGrade,
            onTimeCount,
            lateCount,
            projectGrades
        );
    }

    @Transactional(readOnly = true)
    public GradingTimeStats getGradingTimeStats(UUID projectId) {
        List<Submission> submissions = submissionPort.findByProjectId(projectId);

        List<Duration> gradingDurations = submissions.stream()
            .filter(s -> s.getStatus() == SubmissionStatus.GRADED)
            .filter(s -> s.getSubmittedAt() != null)
            .map(s -> Duration.between(s.getSubmittedAt(), s.getLastModifiedDate()))
            .toList();

        Duration averageTime = gradingDurations.isEmpty() ? Duration.ZERO :
            Duration.ofMillis((long) gradingDurations.stream()
                .mapToLong(Duration::toMillis)
                .average()
                .orElse(0.0));

        Duration maxTime = gradingDurations.isEmpty() ? Duration.ZERO :
            gradingDurations.stream()
                .max(Duration::compareTo)
                .orElse(Duration.ZERO);

        Duration minTime = gradingDurations.isEmpty() ? Duration.ZERO :
            gradingDurations.stream()
                .min(Duration::compareTo)
                .orElse(Duration.ZERO);

        return new GradingTimeStats(
            projectId,
            averageTime,
            maxTime,
            minTime,
            gradingDurations.size()
        );
    }

    public record ProjectSubmissionStats(
        UUID projectId,
        long totalSubmissions,
        long gradedSubmissions,
        long lateSubmissions,
        double averageGrade,
        Map<SubmissionStatus, Long> statusDistribution
    ) {}

    public record StudentSubmissionStats(
        UUID studentId,
        long totalSubmissions,
        double averageGrade,
        long onTimeSubmissions,
        long lateSubmissions,
        Map<UUID, Integer> projectGrades
    ) {}

    public record GradingTimeStats(
        UUID projectId,
        Duration averageGradingTime,
        Duration maxGradingTime,
        Duration minGradingTime,
        int gradedSubmissionsCount
    ) {}
}
