package com.projecthub.base.shared.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;

/**
 * Base implementation for async event publishers.
 * Provides common async event publishing functionality using RabbitMQ.
 * @param <T> The type of domain event
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseAsyncEventPublisher<T> implements DomainEventPublisher<T> {
    private final RabbitTemplate rabbitTemplate;
    private final String executorName;

    @Async
    @Override
    public void publish(final T event) {
        try {
            log.debug("Publishing async event: {}", event);

            final MessagePostProcessor messagePostProcessor = message -> {
                final MessageProperties props = message.getMessageProperties();
                props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                return message;
            };

            this.rabbitTemplate.convertAndSend(
                this.getExchange(),
                this.getRoutingKey(event),
                event,
                messagePostProcessor
            );

            log.debug("Successfully published event: {}", event);
        } catch (final AmqpException e) {
            log.error("Failed to publish event: {}", event, e);
            throw new MessagePublishException("Failed to publish event", e);
        }
    }
}