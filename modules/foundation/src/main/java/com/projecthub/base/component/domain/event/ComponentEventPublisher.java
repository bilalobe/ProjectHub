package com.projecthub.base.component.domain.event;

import java.time.Instant;
import java.util.UUID;

public interface ComponentEventPublisher {
    void publish(ComponentDomainEvent event);

    default void publishCreated(final Component component, final UUID initiatorId) {
        this.publish(new ComponentDomainEvent.Created(
            UUID.randomUUID(),
            component.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    default void publishUpdated(final Component component, final UUID initiatorId) {
        this.publish(new ComponentDomainEvent.Updated(
            UUID.randomUUID(),
            component.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    default void publishDeleted(final UUID componentId, final UUID initiatorId) {
        this.publish(new ComponentDomainEvent.Deleted(
            UUID.randomUUID(),
            componentId,
            initiatorId,
            Instant.now()
        ));
    }
}
