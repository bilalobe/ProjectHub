package com.projecthub.base.project.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectRabbitMQConfig {
    
    public static final String PROJECT_EXCHANGE = "projecthub.project.exchange";
    public static final String PROJECT_STATUS_QUEUE = "projecthub.project.status";
    public static final String PROJECT_CREATED_QUEUE = "projecthub.project.created";
    public static final String PROJECT_UPDATED_QUEUE = "projecthub.project.updated";
    public static final String PROJECT_DELETED_QUEUE = "projecthub.project.deleted";

    @Bean
    public TopicExchange projectExchange() {
        return new TopicExchange(PROJECT_EXCHANGE);
    }

    @Bean
    public Queue projectStatusQueue() {
        return QueueBuilder.durable(PROJECT_STATUS_QUEUE)
            .withArgument("x-dead-letter-exchange", "projecthub.project.dlx")
            .withArgument("x-dead-letter-routing-key", "project.status.dlq")
            .build();
    }

    @Bean
    public Queue projectCreatedQueue() {
        return QueueBuilder.durable(PROJECT_CREATED_QUEUE)
            .withArgument("x-dead-letter-exchange", "projecthub.project.dlx")
            .withArgument("x-dead-letter-routing-key", "project.created.dlq")
            .build();
    }

    @Bean
    public Queue projectUpdatedQueue() {
        return QueueBuilder.durable(PROJECT_UPDATED_QUEUE)
            .withArgument("x-dead-letter-exchange", "projecthub.project.dlx")
            .withArgument("x-dead-letter-routing-key", "project.updated.dlq")
            .build();
    }

    @Bean
    public Queue projectDeletedQueue() {
        return QueueBuilder.durable(PROJECT_DELETED_QUEUE)
            .withArgument("x-dead-letter-exchange", "projecthub.project.dlx")
            .withArgument("x-dead-letter-routing-key", "project.deleted.dlq")
            .build();
    }

    @Bean
    public Binding projectStatusBinding(Queue projectStatusQueue, TopicExchange projectExchange) {
        return BindingBuilder
            .bind(projectStatusQueue)
            .to(projectExchange)
            .with("project.status.*");
    }

    @Bean
    public Binding projectCreatedBinding(Queue projectCreatedQueue, TopicExchange projectExchange) {
        return BindingBuilder
            .bind(projectCreatedQueue)
            .to(projectExchange)
            .with("project.created");
    }

    @Bean
    public Binding projectUpdatedBinding(Queue projectUpdatedQueue, TopicExchange projectExchange) {
        return BindingBuilder
            .bind(projectUpdatedQueue)
            .to(projectExchange)
            .with("project.updated");
    }

    @Bean
    public Binding projectDeletedBinding(Queue projectDeletedQueue, TopicExchange projectExchange) {
        return BindingBuilder
            .bind(projectDeletedQueue)
            .to(projectExchange)
            .with("project.deleted");
    }
}