package com.projecthub.base.submission.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubmissionMessagingConfig {
    public static final String SUBMISSION_EXCHANGE = "submission.exchange";
    public static final String SUBMISSION_CREATED_QUEUE = "submission.created";
    public static final String SUBMISSION_UPDATED_QUEUE = "submission.updated";
    public static final String SUBMISSION_SUBMITTED_QUEUE = "submission.submitted";
    public static final String SUBMISSION_GRADED_QUEUE = "submission.graded";
    public static final String SUBMISSION_REVOKED_QUEUE = "submission.revoked";
    public static final String SUBMISSION_COMMENT_QUEUE = "submission.comment";

    public SubmissionMessagingConfig() {
    }

    @Bean
    public TopicExchange submissionExchange() {
        return new TopicExchange(SUBMISSION_EXCHANGE);
    }

    @Bean
    public Queue submissionCreatedQueue() {
        return new Queue(SUBMISSION_CREATED_QUEUE);
    }

    @Bean
    public Queue submissionUpdatedQueue() {
        return new Queue(SUBMISSION_UPDATED_QUEUE);
    }

    @Bean
    public Queue submissionSubmittedQueue() {
        return new Queue(SUBMISSION_SUBMITTED_QUEUE);
    }

    @Bean
    public Queue submissionGradedQueue() {
        return new Queue(SUBMISSION_GRADED_QUEUE);
    }

    @Bean
    public Queue submissionRevokedQueue() {
        return new Queue(SUBMISSION_REVOKED_QUEUE);
    }

    @Bean
    public Queue submissionCommentQueue() {
        return new Queue(SUBMISSION_COMMENT_QUEUE);
    }

    @Bean
    public Binding submissionCreatedBinding(Queue submissionCreatedQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(submissionCreatedQueue)
            .to(submissionExchange)
            .with("submission.created");
    }

    @Bean
    public Binding submissionUpdatedBinding(Queue submissionUpdatedQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(submissionUpdatedQueue)
            .to(submissionExchange)
            .with("submission.updated");
    }

    @Bean
    public Binding submissionSubmittedBinding(Queue submissionSubmittedQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(submissionSubmittedQueue)
            .to(submissionExchange)
            .with("submission.submitted");
    }

    @Bean
    public Binding submissionGradedBinding(Queue submissionGradedQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(submissionGradedQueue)
            .to(submissionExchange)
            .with("submission.graded");
    }

    @Bean
    public Binding submissionRevokedBinding(Queue submissionRevokedQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(submissionRevokedQueue)
            .to(submissionExchange)
            .with("submission.revoked");
    }

    @Bean
    public Binding submissionCommentBinding(Queue submissionCommentQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(submissionCommentQueue)
            .to(submissionExchange)
            .with("submission.comment.*");
    }
}
