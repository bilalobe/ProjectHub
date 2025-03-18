package com.projecthub.base.student.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StudentMessagingConfig {
    // Define exchange names
    public static final String STUDENT_EXCHANGE = "student.events";
    public static final String STUDENT_SYNC_EXCHANGE = "student.sync.exchange";

    // Define queue names for async events
    public static final String STUDENT_CREATED_QUEUE = "student.created";
    public static final String STUDENT_UPDATED_QUEUE = "student.updated";
    public static final String STUDENT_DELETED_QUEUE = "student.deleted";
    public static final String STUDENT_ACTIVATED_QUEUE = "student.activated";
    public static final String STUDENT_TEAM_ASSIGNED_QUEUE = "student.team.assigned";

    public StudentMessagingConfig() {
    }

    // Exchanges
    @Bean
    public TopicExchange studentExchange() {
        return new TopicExchange(STUDENT_EXCHANGE);
    }

    @Bean
    public TopicExchange studentSyncExchange() {
        return new TopicExchange(STUDENT_SYNC_EXCHANGE);
    }

    // Queues
    @Bean
    public Queue studentCreatedQueue() {
        return QueueBuilder.durable(STUDENT_CREATED_QUEUE).build();
    }

    @Bean
    public Queue studentUpdatedQueue() {
        return QueueBuilder.durable(STUDENT_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue studentDeletedQueue() {
        return QueueBuilder.durable(STUDENT_DELETED_QUEUE).build();
    }

    @Bean
    public Queue studentActivatedQueue() {
        return QueueBuilder.durable(STUDENT_ACTIVATED_QUEUE).build();
    }

    @Bean
    public Queue studentTeamAssignedQueue() {
        return QueueBuilder.durable(STUDENT_TEAM_ASSIGNED_QUEUE).build();
    }

    // Bindings for async events
    @Bean
    public Binding studentCreatedBinding(Queue studentCreatedQueue, TopicExchange studentExchange) {
        return BindingBuilder.bind(studentCreatedQueue)
            .to(studentExchange)
            .with("student.created");
    }

    @Bean
    public Binding studentUpdatedBinding(Queue studentUpdatedQueue, TopicExchange studentExchange) {
        return BindingBuilder.bind(studentUpdatedQueue)
            .to(studentExchange)
            .with("student.updated");
    }

    @Bean
    public Binding studentDeletedBinding(Queue studentDeletedQueue, TopicExchange studentExchange) {
        return BindingBuilder.bind(studentDeletedQueue)
            .to(studentExchange)
            .with("student.deleted");
    }

    @Bean
    public Binding studentActivatedBinding(Queue studentActivatedQueue, TopicExchange studentExchange) {
        return BindingBuilder.bind(studentActivatedQueue)
            .to(studentExchange)
            .with("student.activated");
    }

    @Bean
    public Binding studentTeamAssignedBinding(Queue studentTeamAssignedQueue, TopicExchange studentExchange) {
        return BindingBuilder.bind(studentTeamAssignedQueue)
            .to(studentExchange)
            .with("student.team.assigned");
    }
}
