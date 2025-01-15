package com.projecthub.base.milestone.infrastructure.event.config;

import com.projecthub.base.shared.config.BaseRabbitMQConfig;
import com.projecthub.base.shared.config.RabbitMQProperties;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MilestoneRabbitMQConfig extends BaseRabbitMQConfig {

    public static final String MILESTONE_EXCHANGE = "milestone.exchange";
    public static final String MILESTONE_DLX = "milestone.dlx";
    public static final String MILESTONE_DLQ = "milestone.dlq";

    // Queue names
    public static final String MILESTONE_CREATED_QUEUE = "milestone.created.queue";
    public static final String MILESTONE_UPDATED_QUEUE = "milestone.updated.queue";
    public static final String MILESTONE_DELETED_QUEUE = "milestone.deleted.queue";
    public static final String MILESTONE_COMPLETED_QUEUE = "milestone.completed.queue";


    // Routing keys
    public static final String MILESTONE_CREATED_KEY = "milestone.created";
    public static final String MILESTONE_UPDATED_KEY = "milestone.updated";
    public static final String MILESTONE_DELETED_KEY = "milestone.deleted";
    public static final String MILESTONE_COMPLETED_KEY = "milestone.completed";

    public MilestoneRabbitMQConfig(final RabbitMQProperties rabbitMQProperties) {
        super(rabbitMQProperties);
    }

    @Bean
    public TopicExchange milestoneExchange() {
        return ExchangeBuilder.topicExchange(MilestoneRabbitMQConfig.MILESTONE_EXCHANGE)
            .durable(true)
            .build();
    }

    @Bean
    public TopicExchange milestoneDLX() {
        return ExchangeBuilder.topicExchange(MilestoneRabbitMQConfig.MILESTONE_DLX)
            .durable(true)
            .build();
    }

    @Bean
    public Queue milestoneDLQ() {
        return QueueBuilder.durable(MilestoneRabbitMQConfig.MILESTONE_DLQ)
            .build();
    }

    @Bean
    public Binding milestoneDLQBinding() {
        return BindingBuilder.bind(this.milestoneDLQ())
            .to(this.milestoneDLX())
            .with("#"); // Catch all routing keys
    }

    // Queue definitions
    @Bean
    public Queue milestoneCreatedQueue() {
        return QueueBuilder.durable(MilestoneRabbitMQConfig.MILESTONE_CREATED_QUEUE)
            .withArgument("x-dead-letter-exchange", MilestoneRabbitMQConfig.MILESTONE_DLX)
            .withArgument("x-dead-letter-routing-key", "milestone.created.dead")
            .withArgument("x-message-ttl", 86400000) // 24 hours
            .build();
    }

    @Bean
    public Queue milestoneUpdatedQueue() {
        return QueueBuilder.durable(MilestoneRabbitMQConfig.MILESTONE_UPDATED_QUEUE)
            .withArgument("x-dead-letter-exchange", MilestoneRabbitMQConfig.MILESTONE_DLX)
            .withArgument("x-dead-letter-routing-key", "milestone.updated.dead")
            .withArgument("x-message-ttl", 86400000)
            .build();
    }

    @Bean
    public Queue milestoneDeletedQueue() {
        return QueueBuilder.durable(MilestoneRabbitMQConfig.MILESTONE_DELETED_QUEUE)
            .withArgument("x-dead-letter-exchange", MilestoneRabbitMQConfig.MILESTONE_DLX)
            .withArgument("x-dead-letter-routing-key", "milestone.deleted.dead")
            .withArgument("x-message-ttl", 86400000)
            .build();
    }

    @Bean
    public Queue milestoneCompletedQueue() {
        return QueueBuilder.durable(MilestoneRabbitMQConfig.MILESTONE_COMPLETED_QUEUE)
            .withArgument("x-dead-letter-exchange", MilestoneRabbitMQConfig.MILESTONE_DLX)
            .withArgument("x-dead-letter-routing-key", "milestone.completed.dead")
            .withArgument("x-message-ttl", 86400000)
            .build();
    }


    // Binding definitions
    @Bean
    public Binding milestoneCreatedBinding(final Queue milestoneCreatedQueue, final TopicExchange milestoneExchange) {
        return BindingBuilder.bind(milestoneCreatedQueue)
            .to(milestoneExchange)
            .with(MilestoneRabbitMQConfig.MILESTONE_CREATED_KEY);
    }

    @Bean
    public Binding milestoneUpdatedBinding(final Queue milestoneUpdatedQueue, final TopicExchange milestoneExchange) {
        return BindingBuilder.bind(milestoneUpdatedQueue)
            .to(milestoneExchange)
            .with(MilestoneRabbitMQConfig.MILESTONE_UPDATED_KEY);
    }

    @Bean
    public Binding milestoneDeletedBinding(final Queue milestoneDeletedQueue, final TopicExchange milestoneExchange) {
        return BindingBuilder.bind(milestoneDeletedQueue)
            .to(milestoneExchange)
            .with(MilestoneRabbitMQConfig.MILESTONE_DELETED_KEY);
    }

    @Bean
    public Binding milestoneCompletedBinding(final Queue milestoneCompletedQueue, final TopicExchange milestoneExchange) {
        return BindingBuilder.bind(milestoneCompletedQueue)
            .to(milestoneExchange)
            .with(MilestoneRabbitMQConfig.MILESTONE_COMPLETED_KEY);
    }
}
