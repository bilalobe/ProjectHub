package com.projecthub.base.school.infrastructure.event.adapter;

import com.projecthub.base.school.domain.event.SchoolDomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncSchoolEventAdapter implements SchoolEventAdapter {
    private static final String EXCHANGE = "school.exchange";
    private final RabbitTemplate rabbitTemplate;

    @Async("schoolEventExecutor")
    @Override
    public void publish(SchoolDomainEvent event) {
        log.debug("Publishing async event: {}", event);
        rabbitTemplate.convertAndSend(
            getExchange(),
            getRoutingKey(event),
            event
        );
    }

    @Override
    public String getRoutingKey(SchoolDomainEvent event) {
        return switch (event) {
            case SchoolDomainEvent.Created e -> "school.created";
            case SchoolDomainEvent.Updated e -> "school.updated";
            case SchoolDomainEvent.Deleted e -> "school.deleted";
            case SchoolDomainEvent.Archived e -> "school.archived";
            case SchoolDomainEvent.CohortAdded e -> "school.cohort.added";
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getClass().getSimpleName());
        };
    }


    @Override
    public String getExchange() {
        return EXCHANGE;
    }
}
