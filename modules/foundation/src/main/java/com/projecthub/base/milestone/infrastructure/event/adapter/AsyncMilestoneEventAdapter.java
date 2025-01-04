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
    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE = MilestoneRabbitMQConfig.MILESTONE_EXCHANGE;

    @Async("milestoneEventExecutor")
    @Override
    public void publish(MilestoneDomainEvent event) {
        try {
            log.debug("Publishing milestone event: {}", event);
            
            MessagePostProcessor messagePostProcessor = message -> {
                MessageProperties props = message.getMessageProperties();
                props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                return message;
            };

            rabbitTemplate.convertAndSend(
                EXCHANGE,
                getRoutingKey(event),
                event,
                messagePostProcessor
            );

            log.debug("Successfully published milestone event: {}", event);
        } catch (AmqpException e) {
            log.error("Failed to publish milestone event: {}", event, e);
            // Consider implementing retry logic or storing failed events
            throw new MessagePublishException("Failed to publish milestone event", e);
        }
    }

    @Override
    public String getRoutingKey(MilestoneDomainEvent event) {
        return switch (event) {
            case MilestoneDomainEvent.Created e ->  MilestoneRabbitMQConfig.MILESTONE_CREATED_KEY;
            case MilestoneDomainEvent.Updated e ->  MilestoneRabbitMQConfig.MILESTONE_UPDATED_KEY;
            case MilestoneDomainEvent.Deleted e ->  MilestoneRabbitMQConfig.MILESTONE_DELETED_KEY;
            case MilestoneDomainEvent.Completed e ->  MilestoneRabbitMQConfig.MILESTONE_COMPLETED_KEY;
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getClass().getSimpleName());
        };
    }

    @Override
    public String getExchange() {
        return EXCHANGE;
    }
}
