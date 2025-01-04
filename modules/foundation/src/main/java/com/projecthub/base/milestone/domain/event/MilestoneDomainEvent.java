package com.projecthub.base.milestone.domain.event;

import java.time.Instant;
import java.util.UUID;

public sealed interface MilestoneDomainEvent {
    record Created(
        UUID eventId,
        UUID milestoneId,
        UUID initiatorId,
        Instant occurredOn
    ) implements MilestoneDomainEvent {
    }

    record Updated(
        UUID eventId,
        UUID milestoneId,
        UUID initiatorId,
        Instant occurredOn
    ) implements MilestoneDomainEvent {
    }

    record Deleted(
        UUID eventId,
        UUID milestoneId,
        UUID initiatorId,
        Instant occurredOn
    ) implements MilestoneDomainEvent {
    }

    record Completed(
        UUID eventId,
        UUID milestoneId,
        UUID initiatorId,
        Instant completedAt,
        Instant occurredOn
    ) implements MilestoneDomainEvent {
    }
}
