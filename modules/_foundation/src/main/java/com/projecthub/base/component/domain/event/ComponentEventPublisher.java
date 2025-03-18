package com.projecthub.base.component.domain.event;

import com.projecthub.base.component.domain.entity.Component;
import com.projecthub.base.shared.infrastructure.event.DomainEventPublisher;

import java.util.UUID;

/**
 * Publisher interface for component domain events.
 */
public interface ComponentEventPublisher extends DomainEventPublisher<ComponentDomainEvent> {
    default void publishCreated(Component component, UUID initiatorId) {
        publish(new ComponentDomainEvent.Created(
            generateEventId(),
            component.getId(),
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishUpdated(Component component, UUID initiatorId) {
        publish(new ComponentDomainEvent.Updated(
            generateEventId(),
            component.getId(),
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishDeleted(UUID componentId, UUID initiatorId) {
        publish(new ComponentDomainEvent.Deleted(
            generateEventId(),
            componentId,
            initiatorId,
            getTimestamp()
        ));
    }
}
