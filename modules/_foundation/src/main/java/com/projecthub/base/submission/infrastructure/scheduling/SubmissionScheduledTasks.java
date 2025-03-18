package com.projecthub.base.submission.infrastructure.scheduling;

import com.projecthub.base.submission.application.service.SubmissionAnalyticsService;
import com.projecthub.base.submission.application.service.SubmissionBatchService;
import com.projecthub.base.submission.application.service.SubmissionQueryService;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.infrastructure.notification.SubmissionNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionScheduledTasks {
    private final SubmissionQueryService queryService;
    private final SubmissionBatchService batchService;
    private final SubmissionAnalyticsService analyticsService;
    private final SubmissionNotificationService notificationService;

    @Scheduled(cron = "0 0 */1 * * *") // Every hour
    @Transactional(readOnly = true)
    public void checkOverdueSubmissions() {
        log.info("Checking for overdue submissions");

        // In a real implementation, you would get active projects from a project service
        List<UUID> activeProjects = List.of(); // Get from project service

        for (UUID projectId : activeProjects) {
            CompletableFuture<List<UUID>> overdueSubmissionsFuture =
                batchService.findOverdueSubmissions(projectId);

            overdueSubmissionsFuture.thenAccept(overdueSubmissions -> {
                log.info("Found {} overdue submissions for project {}",
                        Integer.valueOf(overdueSubmissions.size()), projectId);

                overdueSubmissions.forEach(submissionId ->
                    queryService.findById(submissionId).ifPresent(submission ->
                        notificationService.notifySubmissionOverdue(submission)
                    )
                );
            });
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // Every day at midnight
    @Transactional(readOnly = true)
    public void generateDailyAnalytics() {
        log.info("Generating daily submission analytics");

        // In a real implementation, you would get active projects from a project service
        List<UUID> activeProjects = List.of(); // Get from project service

        for (UUID projectId : activeProjects) {
            try {
                var stats = analyticsService.getProjectStats(projectId);
                var gradingStats = analyticsService.getGradingTimeStats(projectId);

                log.info("Project {} stats: {} submissions, {} graded, avg grade {}",
                    projectId,
                        Long.valueOf(stats.totalSubmissions()),
                        Long.valueOf(stats.gradedSubmissions()),
                        Double.valueOf(stats.averageGrade())
                );

                log.info("Project {} grading stats: avg time {}, {} graded submissions",
                    projectId,
                    gradingStats.averageGradingTime(),
                        Integer.valueOf(gradingStats.gradedSubmissionsCount())
                );

            } catch (RuntimeException e) {
                log.error("Failed to generate analytics for project {}", projectId, e);
            }
        }
    }

    @Scheduled(cron = "0 0 12 * * MON") // Every Monday at noon
    @Transactional(readOnly = true)
    public void checkLongPendingSubmissions() {
        log.info("Checking for submissions pending review for too long");

        // In a real implementation, you would get active projects from a project service
        List<UUID> activeProjects = List.of(); // Get from project service

        for (UUID projectId : activeProjects) {
            CompletableFuture<List<UUID>> ungradedFuture =
                batchService.findUngraded(projectId);

            ungradedFuture.thenAccept(ungraded -> {
                log.info("Found {} ungraded submissions for project {}",
                        Integer.valueOf(ungraded.size()), projectId);

                // Send reminders to instructors
                notifyInstructorsAboutPendingSubmissions(projectId, ungraded.size());
            });
        }
    }

    @Scheduled(cron = "0 0 1 * * *") // Every day at 1 AM
    @Transactional
    public static void cleanupOldDrafts() {
        log.info("Cleaning up old draft submissions");

        Instant oneMonthAgo = Instant.now().minus(java.time.Duration.ofDays(30L));

        // In a real implementation, you would get this from a repository method
        List<Submission> oldDrafts = List.of(); // Get old drafts

        for (Submission draft : oldDrafts) {
            log.info("Archiving old draft submission {}", draft.getSubmissionId());
            // Archive or delete old drafts based on your retention policy
        }
    }

    private void notifyInstructorsAboutPendingSubmissions(UUID projectId, int count) {
        String subject = "Pending Submissions Reminder";
        String message = String.format("""
            Project %s has %d submissions pending review.
            Please review these submissions at your earliest convenience.
            """,
            projectId, Integer.valueOf(count)
        );

        // In a real implementation, you would get instructor IDs from a project service
        List<UUID> instructors = List.of(); // Get from project service

        for (UUID instructor : instructors) {
            try {
                notificationService.notifySubmissionOverdue(null); // Replace with proper notification
            } catch (RuntimeException e) {
                log.error("Failed to notify instructor {} about pending submissions",
                    instructor, e);
            }
        }
    }
}
