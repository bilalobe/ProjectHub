package com.projecthub.base.submission.infrastructure.event.config;

import com.projecthub.base.shared.config.BaseRabbitMQConfig;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration

public class SubmissionRabbitMQConfig extends BaseRabbitMQConfig {

    public SubmissionRabbitMQConfig(RabbitMQProperties rabbitMQProperties) {
        super(rabbitMQProperties);
    }

    public static final String SUBMISSION_EXCHANGE = "submission.exchange";

    // Queue names
    public static final String SUBMISSION_CREATED_QUEUE = "submission.created.queue";
    public static final String SUBMISSION_UPDATED_QUEUE = "submission.updated.queue";
    public static final String SUBMISSION_DELETED_QUEUE = "submission.deleted.queue";

    // Dead-letter exchange and queue
    public static final String SUBMISSION_DLX = "submission.dlx";
    public static final String SUBMISSION_DLQ = "submission.dlq";

    // Routing keys
    public static final String SUBMISSION_CREATED_KEY = "submission.created";
    public static final String SUBMISSION_UPDATED_KEY = "submission.updated";
    public static final String SUBMISSION_DELETED_KEY = "submission.deleted";

    @Bean
    public TopicExchange submissionExchange() {
        return ExchangeBuilder.topicExchange(SUBMISSION_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public TopicExchange submissionDeadLetterExchange() {
        return ExchangeBuilder.topicExchange(SUBMISSION_DLX)
                .durable(true)
                .build();
    }

    @Bean
    public Queue submissionDeadLetterQueue() {
        return QueueBuilder.durable(SUBMISSION_DLQ)
                .build();
    }

    @Bean
    public Binding submissionDeadLetterBinding() {
        return BindingBuilder.bind(submissionDeadLetterQueue())
                .to(submissionDeadLetterExchange())
                .with("#");
    }

    // Queue definitions
    @Bean
    public Queue submissionCreatedQueue() {
        return QueueBuilder.durable(SUBMISSION_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", SUBMISSION_DLX)
                .withArgument("x-dead-letter-routing-key", "submission.created.dead")
                .withArgument("x-message-ttl", 86400000) // 24 hours
                .build();
    }

    @Bean
    public Queue submissionUpdatedQueue() {
        return QueueBuilder.durable(SUBMISSION_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue submissionDeletedQueue() {
        return QueueBuilder.durable(SUBMISSION_DELETED_QUEUE).build();
    }

    // Binding definitions
    @Bean
    public Binding submissionCreatedBinding(Queue submissionCreatedQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(submissionCreatedQueue)
                .to(submissionExchange)
                .with(SUBMISSION_CREATED_KEY);
    }

    @Bean
    public Binding submissionUpdatedBinding(Queue submissionUpdatedQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(submissionUpdatedQueue)
                .to(submissionExchange)
                .with(SUBMISSION_UPDATED_KEY);
    }

    @Bean
    public Binding submissionDeletedBinding(Queue submissionDeletedQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(submissionDeletedQueue)
                .to(submissionExchange)
                .with(SUBMISSION_DELETED_KEY);
    }
}