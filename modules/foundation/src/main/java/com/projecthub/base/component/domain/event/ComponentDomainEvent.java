package com.projecthub.base.component.domain.event;

import java.time.Instant;
import java.util.UUID;

public sealed interface ComponentDomainEvent {

    record Created(
        UUID eventId,
        UUID componentId,
        UUID initiatorId,
        Instant occurredOn
    ) implements ComponentDomainEvent {
    }

    record Updated(
        UUID eventId,
        UUID componentId,
        UUID initiatorId,
        Instant occurredOn
    ) implements ComponentDomainEvent {
    }

    record Deleted(
        UUID eventId,
        UUID componentId,
        UUID initiatorId,
        Instant occurredOn
    ) implements ComponentDomainEvent {
    }
}
