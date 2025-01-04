package com.projecthub.base.cohort.infrastructure.event.config;

import com.projecthub.base.shared.config.BaseRabbitMQConfig;
import com.projecthub.base.shared.config.RabbitMQProperties;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CohortRabbitMQConfig extends BaseRabbitMQConfig {

    public static final String COHORT_EXCHANGE = "cohort.exchange";

    // Queue names
    public static final String COHORT_CREATED_QUEUE = "cohort.created.queue";
    public static final String COHORT_UPDATED_QUEUE = "cohort.updated.queue";
    public static final String COHORT_DELETED_QUEUE = "cohort.deleted.queue";
    public static final String COHORT_ARCHIVED_QUEUE = "cohort.archived.queue";
    public static final String COHORT_STUDENT_ADDED_QUEUE = "cohort.student.added.queue";
    public static final String COHORT_STUDENT_REMOVED_QUEUE = "cohort.student.removed.queue";

    // Routing keys
    public static final String COHORT_CREATED_KEY = "cohort.created";
    public static final String COHORT_UPDATED_KEY = "cohort.updated";
    public static final String COHORT_DELETED_KEY = "cohort.deleted";
    public static final String COHORT_ARCHIVED_KEY = "cohort.archived";
    public static final String COHORT_STUDENT_ADDED_KEY = "cohort.student.added";
    public static final String COHORT_STUDENT_REMOVED_KEY = "cohort.student.removed";


    public CohortRabbitMQConfig(RabbitMQProperties properties) {
        super(properties);
    }

    @Bean
    public TopicExchange cohortExchange() {
        return ExchangeBuilder.topicExchange(COHORT_EXCHANGE)
            .durable(true)
            .build();
    }

    // Queue definitions
    @Bean
    public Queue cohortCreatedQueue() {
        return QueueBuilder.durable(COHORT_CREATED_QUEUE).build();
    }

    @Bean
    public Queue cohortUpdatedQueue() {
        return QueueBuilder.durable(COHORT_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue cohortDeletedQueue() {
        return QueueBuilder.durable(COHORT_DELETED_QUEUE).build();
    }

    @Bean
    public Queue cohortArchivedQueue() {
        return QueueBuilder.durable(COHORT_ARCHIVED_QUEUE).build();
    }

    @Bean
    public Queue cohortStudentAddedQueue() {
        return QueueBuilder.durable(COHORT_STUDENT_ADDED_QUEUE).build();
    }

    @Bean
    public Queue cohortStudentRemovedQueue() {
        return QueueBuilder.durable(COHORT_STUDENT_REMOVED_QUEUE).build();
    }

    // Binding definitions
    @Bean
    public Binding cohortCreatedBinding(Queue cohortCreatedQueue, TopicExchange cohortExchange) {
        return BindingBuilder.bind(cohortCreatedQueue)
            .to(cohortExchange)
            .with(COHORT_CREATED_KEY);
    }

    @Bean
    public Binding cohortUpdatedBinding(Queue cohortUpdatedQueue, TopicExchange cohortExchange) {
        return BindingBuilder.bind(cohortUpdatedQueue)
            .to(cohortExchange)
            .with(COHORT_UPDATED_KEY);
    }

    @Bean
    public Binding cohortDeletedBinding(Queue cohortDeletedQueue, TopicExchange cohortExchange) {
        return BindingBuilder.bind(cohortDeletedQueue)
            .to(cohortExchange)
            .with(COHORT_DELETED_KEY);
    }

    @Bean
    public Binding cohortArchivedBinding(Queue cohortArchivedQueue, TopicExchange cohortExchange) {
        return BindingBuilder.bind(cohortArchivedQueue)
            .to(cohortExchange)
            .with(COHORT_ARCHIVED_KEY);
    }

    @Bean
    public Binding cohortStudentAddedBinding(Queue cohortStudentAddedQueue, TopicExchange cohortExchange) {
        return BindingBuilder.bind(cohortStudentAddedQueue)
            .to(cohortExchange)
            .with(COHORT_STUDENT_ADDED_KEY);
    }

    @Bean
    public Binding cohortStudentRemovedBinding(Queue cohortStudentRemovedQueue, TopicExchange cohortExchange) {
        return BindingBuilder.bind(cohortStudentRemovedQueue)
            .to(cohortExchange)
            .with(COHORT_STUDENT_REMOVED_KEY);
    }
}
