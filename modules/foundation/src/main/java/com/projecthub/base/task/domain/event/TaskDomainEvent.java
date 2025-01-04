package com.projecthub.base.task.domain.event;

import java.time.Instant;
import java.util.UUID;

public sealed interface TaskDomainEvent {

    record Created(
        UUID eventId,
        UUID taskId,
        UUID initiatorId,
        Instant occurredOn
    ) implements TaskDomainEvent {
    }

    record Updated(
        UUID eventId,
        UUID taskId,
        UUID initiatorId,
        Instant occurredOn
    ) implements TaskDomainEvent {
    }

    record Deleted(
        UUID eventId,
        UUID taskId,
        UUID initiatorId,
        Instant occurredOn
    ) implements TaskDomainEvent {
    }
}
