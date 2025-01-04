package com.projecthub.base.task.infrastructure.event;

import com.projecthub.base.shared.config.RabbitMQConfig;
import com.projecthub.base.task.domain.event.TaskDomainEvent;
import com.projecthub.base.task.domain.event.TaskEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskEventPublisherImpl implements TaskEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(TaskDomainEvent event) {
        log.debug("Publishing task event to RabbitMQ: {}", event);
        try {
            String routingKey = determineRoutingKey(event);
            rabbitTemplate.convertAndSend(RabbitMQConfig.TASK_EXCHANGE, routingKey, event);
        } catch (Exception e) {
            log.error("Error publishing task event: {}", event, e);
        }
    }

    private String determineRoutingKey(TaskDomainEvent event) {
        return switch (event) {
            case TaskDomainEvent.Created _ -> "task.created";
            case TaskDomainEvent.Updated _ -> "task.updated";
            case TaskDomainEvent.Deleted _ -> "task.deleted";
        };
    }
}
