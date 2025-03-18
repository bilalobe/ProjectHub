package com.projecthub.base.submission.infrastructure.notification;

import com.projecthub.base.shared.notification.EmailService;
import com.projecthub.base.shared.notification.NotificationChannel;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.domain.value.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionNotificationService {
    private final EmailService emailService;
    private final NotificationChannel pushNotifications;

    @Async
    public CompletableFuture<Void> notifySubmissionCreated(Submission submission) {
        log.debug("Sending submission created notification for submission {}", submission.getSubmissionId());

        return CompletableFuture.runAsync(() -> {
            String subject = "New Submission Created";
            String message = String.format("""
                A new submission has been created:
                Student ID: %s
                Project ID: %s
                Status: %s
                """,
                submission.getStudentId(),
                submission.getProjectId(),
                submission.getStatus()
            );

            notifyStakeholders(submission, subject, message);
        });
    }

    @Async
    public CompletableFuture<Void> notifySubmissionGraded(Submission submission) {
        log.debug("Sending submission graded notification for submission {}", submission.getSubmissionId());

        return CompletableFuture.runAsync(() -> {
            String subject = "Submission Graded";
            String message = String.format("""
                Your submission has been graded:
                Grade: %d
                Feedback: %s
                """,
                submission.getGrade(),
                submission.getFeedback()
            );

            emailService.sendEmail(submission.getStudentId(), subject, message);
            pushNotifications.send(submission.getStudentId(), subject, message);
        });
    }

    @Async
    public CompletableFuture<Void> notifySubmissionRevoked(Submission submission, String reason) {
        log.debug("Sending submission revoked notification for submission {}", submission.getSubmissionId());

        return CompletableFuture.runAsync(() -> {
            String subject = "Submission Revoked";
            String message = String.format("""
                Your submission has been revoked:
                Reason: %s
                Please contact your instructor for more information.
                """,
                reason
            );

            emailService.sendEmail(submission.getStudentId(), subject, message);
            pushNotifications.send(submission.getStudentId(), subject, message);
        });
    }

    @Async
    public CompletableFuture<Void> notifyNewComment(Submission submission, Comment comment) {
        log.debug("Sending new comment notification for submission {}", submission.getSubmissionId());

        return CompletableFuture.runAsync(() -> {
            String subject = "New Comment on Submission";
            String message = String.format("""
                A new comment has been added to your submission:
                Comment: %s
                """,
                comment.getText()
            );

            Set<UUID> recipients = getCommentStakeholders(submission, comment);
            for (UUID recipient : recipients) {
                emailService.sendEmail(recipient, subject, message);
                pushNotifications.send(recipient, subject, message);
            }
        });
    }

    @Async
    public CompletableFuture<Void> notifySubmissionOverdue(Submission submission) {
        log.debug("Sending submission overdue notification for submission {}", submission.getSubmissionId());

        return CompletableFuture.runAsync(() -> {
            String subject = "Submission is Overdue";
            String message = """
                Your submission is now overdue. Please submit as soon as possible or contact your instructor.
                """;

            emailService.sendEmail(submission.getStudentId(), subject, message);
            pushNotifications.send(submission.getStudentId(), subject, "Your submission is overdue");
        });
    }

    private void notifyStakeholders(Submission submission, String subject, String message) {
        // Notify student
        emailService.sendEmail(submission.getStudentId(), subject, message);
        pushNotifications.send(submission.getStudentId(), subject, message);

        // Notify instructors (in a real implementation, you would get this from a service)
        Set<UUID> instructors = Set.of(); // Get instructors for the project
        for (UUID instructor : instructors) {
            emailService.sendEmail(instructor, subject, message);
            pushNotifications.send(instructor, subject, message);
        }
    }

    private static Set<UUID> getCommentStakeholders(Submission submission, Comment comment) {
        // In a real implementation, you would get all users involved in the submission
        // This might include the student, instructors, and previous commenters
        return Set.of(submission.getStudentId(), comment.getAuthorId());
    }
}
