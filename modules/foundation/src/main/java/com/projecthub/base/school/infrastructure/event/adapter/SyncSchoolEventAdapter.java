package com.projecthub.base.school.infrastructure.event.adapter;

import com.projecthub.base.school.domain.event.SchoolDomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncSchoolEventAdapter implements SchoolEventAdapter {
    private static final String EXCHANGE = "school.sync.exchange";
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(SchoolDomainEvent event) {
        log.debug("Publishing sync event: {}", event);
        rabbitTemplate.convertAndSend(
            getExchange(),
            getRoutingKey(event),
            event
        );
    }

    @Override
    public String getRoutingKey(SchoolDomainEvent event) {
        return switch (event) {
            case SchoolDomainEvent.Created e -> "school.sync.created";
            case SchoolDomainEvent.Updated e -> "school.sync.updated";
            case SchoolDomainEvent.Deleted e -> "school.sync.deleted";
            case SchoolDomainEvent.Archived e -> "school.sync.archived";
            case SchoolDomainEvent.CohortAdded e -> "school.sync.cohort.added";
            default -> "school.sync.unknown";
        };
    }

    @Override
    public String getExchange() {
        return EXCHANGE;
    }
}
