package com.projecthub.base.cohort.infrastructure.event.adapter;

import com.projecthub.base.cohort.domain.event.CohortDomainEvent;
import com.projecthub.base.cohort.domain.event.CohortEventAdapter;
import com.projecthub.base.shared.exception.EventPublishingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component("syncCohortEventAdapter")
@RequiredArgsConstructor
public class SyncCohortEventAdapter implements CohortEventAdapter {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(CohortDomainEvent event) {
        if (event == null) {
            throw new EventPublishingException("Event cannot be null");
        }

        try {
            log.debug("Publishing sync event of type {}: {}", getEventType(event), event);
            publisher.publishEvent(event);
            log.debug("Successfully published sync event: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish sync event: {}", event, e);
            throw new EventPublishingException("Failed to publish event: " + e.getMessage(), e);
        }
    }

    private String getEventType(CohortDomainEvent event) {
        return switch (event) {
            case CohortDomainEvent.Created _ -> "CREATED";
            case CohortDomainEvent.Updated _ -> "UPDATED";
            case CohortDomainEvent.Deleted _ -> "DELETED";
            case CohortDomainEvent.Archived _ -> "ARCHIVED";
            case CohortDomainEvent.StudentAdded _ -> "STUDENT_ADDED";
            case CohortDomainEvent.StudentRemoved _ -> "STUDENT_REMOVED";
        };
    }

    @Override
    public String getRoutingKey(CohortDomainEvent event) {
        throw new UnsupportedOperationException("Routing key not needed for sync events");
    }

    @Override
    public String getExchange() {
        throw new UnsupportedOperationException("Exchange not needed for sync events");
    }
}
