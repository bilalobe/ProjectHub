package com.projecthub.base.milestone.infrastructure.event.adapter;


import com.projecthub.base.milestone.domain.event.MilestoneDomainEvent;
import com.projecthub.base.milestone.domain.event.MilestoneEventAdapter;
import com.projecthub.base.milestone.infrastructure.event.config.MilestoneRabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component("asyncMilestoneEventAdapter")
@RequiredArgsConstructor
public class AsyncMilestoneEventAdapter implements MilestoneEventAdapter {
    private static final String EXCHANGE = MilestoneRabbitMQConfig.MILESTONE_EXCHANGE;
    private final RabbitTemplate rabbitTemplate;

    @Async("milestoneEventExecutor")
    @Override
    public void publish(final MilestoneDomainEvent event) {
        try {
            AsyncMilestoneEventAdapter.log.debug("Publishing milestone event: {}", event);

            final MessagePostProcessor messagePostProcessor = message -> {
                final MessageProperties props = message.getMessageProperties();
                props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                return message;
            };

            this.rabbitTemplate.convertAndSend(
                AsyncMilestoneEventAdapter.EXCHANGE,
                this.getRoutingKey(event),
                event,
                messagePostProcessor
            );

            AsyncMilestoneEventAdapter.log.debug("Successfully published milestone event: {}", event);
        } catch (final AmqpException e) {
            AsyncMilestoneEventAdapter.log.error("Failed to publish milestone event: {}", event, e);
            // Consider implementing retry logic or storing failed events
            throw new MessagePublishException("Failed to publish milestone event", e);
        }
    }

    @Override
    public String getRoutingKey(final MilestoneDomainEvent event) {
        return switch (event) {
            case final MilestoneDomainEvent.Created e -> MilestoneRabbitMQConfig.MILESTONE_CREATED_KEY;
            case final MilestoneDomainEvent.Updated e -> MilestoneRabbitMQConfig.MILESTONE_UPDATED_KEY;
            case final MilestoneDomainEvent.Deleted e -> MilestoneRabbitMQConfig.MILESTONE_DELETED_KEY;
            case final MilestoneDomainEvent.Completed e -> MilestoneRabbitMQConfig.MILESTONE_COMPLETED_KEY;
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getClass().getSimpleName());
        };
    }

    @Override
    public String getExchange() {
        return AsyncMilestoneEventAdapter.EXCHANGE;
    }
}
