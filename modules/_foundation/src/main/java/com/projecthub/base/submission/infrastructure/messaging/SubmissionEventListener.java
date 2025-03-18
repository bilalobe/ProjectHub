package com.projecthub.base.submission.infrastructure.messaging;

import com.projecthub.base.submission.domain.event.SubmissionDomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubmissionEventListener {

    public SubmissionEventListener() {
    }

    @RabbitListener(queues = SubmissionMessagingConfig.SUBMISSION_CREATED_QUEUE)
    public static void handleSubmissionCreated(SubmissionDomainEvent.Created event) {
        log.info("Handling submission created event: {}", event);
        // Add notification or integration logic here
    }

    @RabbitListener(queues = SubmissionMessagingConfig.SUBMISSION_SUBMITTED_QUEUE)
    public static void handleSubmissionSubmitted(SubmissionDomainEvent.Submitted event) {
        log.info("Handling submission submitted event: {}", event);
        // Add notification to instructors/mentors
    }

    @RabbitListener(queues = SubmissionMessagingConfig.SUBMISSION_GRADED_QUEUE)
    public static void handleSubmissionGraded(SubmissionDomainEvent.Graded event) {
        log.info("Handling submission graded event: {}", event);
        // Add student notification
    }

    @RabbitListener(queues = SubmissionMessagingConfig.SUBMISSION_REVOKED_QUEUE)
    public static void handleSubmissionRevoked(SubmissionDomainEvent.Revoked event) {
        log.info("Handling submission revoked event: {}", event);
        // Add notification to student and instructors
    }

    @RabbitListener(queues = SubmissionMessagingConfig.SUBMISSION_COMMENT_QUEUE)
    public static void handleSubmissionComment(SubmissionDomainEvent.CommentAdded event) {
        log.info("Handling submission comment event: {}", event);
        // Add notification to relevant parties
    }
}
