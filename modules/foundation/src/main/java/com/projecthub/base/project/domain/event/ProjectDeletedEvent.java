package com.projecthub.base.project.domain.event;

import java.time.Instant;
import java.util.UUID;


public record ProjectDeletedEvent(
    UUID eventId,
    UUID projectId,
    Instant timestamp,
    UUID initiatorId
) implements ProjectEvent {

    public ProjectDeletedEvent(final UUID projectId, final UUID initiatorId) {
        this(
            UUID.randomUUID(),
            projectId,
            Instant.now(),
            initiatorId
        );
    }

    @Override
    public UUID getEventId() {
        return this.eventId;
    }

    @Override
    public UUID getProjectId() {
        return this.projectId;
    }

    @Override
    public Instant getOccurredOn() {
        return this.timestamp;
    }
}
