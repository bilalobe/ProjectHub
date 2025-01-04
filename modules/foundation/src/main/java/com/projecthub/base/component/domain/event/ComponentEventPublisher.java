package com.projecthub.base.component.domain.event;

import java.time.Instant;
import java.util.UUID;

public interface ComponentEventPublisher {
    void publish(ComponentDomainEvent event);

    default void publishCreated(Component component, UUID initiatorId) {
        publish(new ComponentDomainEvent.Created(
            UUID.randomUUID(),
            component.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    default void publishUpdated(Component component, UUID initiatorId) {
        publish(new ComponentDomainEvent.Updated(
            UUID.randomUUID(),
            component.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    default void publishDeleted(UUID componentId, UUID initiatorId) {
        publish(new ComponentDomainEvent.Deleted(
            UUID.randomUUID(),
            componentId,
            initiatorId,
            Instant.now()
        ));
    }
}
