package com.projecthub.base.task.infrastructure.event;

import com.projecthub.base.shared.config.RabbitMQConfig;
import com.projecthub.base.task.domain.event.TaskDomainEvent;
import com.projecthub.base.task.domain.event.TaskEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskEventPublisherImpl implements TaskEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(final TaskDomainEvent event) {
        TaskEventPublisherImpl.log.debug("Publishing task event to RabbitMQ: {}", event);
        try {
            final String routingKey = TaskEventPublisherImpl.determineRoutingKey(event);
            this.rabbitTemplate.convertAndSend(RabbitMQConfig.TASK_EXCHANGE, routingKey, event);
        } catch (AmqpException e) {
            throw new RuntimeException(e);
        } catch (final Exception e) {
            TaskEventPublisherImpl.log.error("Error publishing task event: {}", event, e);
        }
    }

    private static String determineRoutingKey(final TaskDomainEvent event) {
        return switch (event) {
            case final TaskDomainEvent.Created _ -> "task.created";
            case final TaskDomainEvent.Updated _ -> "task.updated";
            case final TaskDomainEvent.Deleted _ -> "task.deleted";
        };
    }
}
