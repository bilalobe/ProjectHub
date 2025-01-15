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
    public void publish(final SchoolDomainEvent event) {
        SyncSchoolEventAdapter.log.debug("Publishing sync event: {}", event);
        this.rabbitTemplate.convertAndSend(
            EXCHANGE,
            this.getRoutingKey(event),
            event
        );
    }

    @Override
    public String getRoutingKey(final SchoolDomainEvent event) {
        return switch (event) {
            case final SchoolDomainEvent.Created e -> "school.sync.created";
            case final SchoolDomainEvent.Updated e -> "school.sync.updated";
            case final SchoolDomainEvent.Deleted e -> "school.sync.deleted";
            case final SchoolDomainEvent.Archived e -> "school.sync.archived";
            case final SchoolDomainEvent.CohortAdded e -> "school.sync.cohort.added";
            default -> "school.sync.unknown";
        };
    }

    @Override
    public String getExchange() {
        return SyncSchoolEventAdapter.EXCHANGE;
    }
}
