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
    public void publish(final SchoolDomainEvent event) {
        AsyncSchoolEventAdapter.log.debug("Publishing async event: {}", event);
        this.rabbitTemplate.convertAndSend(
            EXCHANGE,
            this.getRoutingKey(event),
            event
        );
    }

    @Override
    public String getRoutingKey(final SchoolDomainEvent event) {
        return switch (event) {
            case final SchoolDomainEvent.Created e -> "school.created";
            case final SchoolDomainEvent.Updated e -> "school.updated";
            case final SchoolDomainEvent.Deleted e -> "school.deleted";
            case final SchoolDomainEvent.Archived e -> "school.archived";
            case final SchoolDomainEvent.CohortAdded e -> "school.cohort.added";
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getClass().getSimpleName());
        };
    }


    @Override
    public String getExchange() {
        return AsyncSchoolEventAdapter.EXCHANGE;
    }
}
