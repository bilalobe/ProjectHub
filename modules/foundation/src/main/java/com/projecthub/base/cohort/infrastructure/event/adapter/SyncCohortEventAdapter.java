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
    public void publish(final CohortDomainEvent event) {
        if (null == event) {
            throw new EventPublishingException("Event cannot be null");
        }

        try {
            SyncCohortEventAdapter.log.debug("Publishing sync event of type {}: {}", this.getEventType(event), event);
            this.publisher.publishEvent(event);
            SyncCohortEventAdapter.log.debug("Successfully published sync event: {}", event);
        } catch (final Exception e) {
            SyncCohortEventAdapter.log.error("Failed to publish sync event: {}", event, e);
            throw new EventPublishingException("Failed to publish event: " + e.getMessage(), e);
        }
    }

    private String getEventType(final CohortDomainEvent event) {
        return switch (event) {
            case final CohortDomainEvent.Created _ -> "CREATED";
            case final CohortDomainEvent.Updated _ -> "UPDATED";
            case final CohortDomainEvent.Deleted _ -> "DELETED";
            case final CohortDomainEvent.Archived _ -> "ARCHIVED";
            case final CohortDomainEvent.StudentAdded _ -> "STUDENT_ADDED";
            case final CohortDomainEvent.StudentRemoved _ -> "STUDENT_REMOVED";
        };
    }

    @Override
    public String getRoutingKey(final CohortDomainEvent event) {
        throw new UnsupportedOperationException("Routing key not needed for sync events");
    }

    @Override
    public String getExchange() {
        throw new UnsupportedOperationException("Exchange not needed for sync events");
    }
}
