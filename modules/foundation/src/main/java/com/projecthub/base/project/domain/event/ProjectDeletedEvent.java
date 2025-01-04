package com.projecthub.base.project.domain.event;

import java.time.Instant;
import java.util.UUID;


public record ProjectDeletedEvent(
    UUID eventId,
    UUID projectId,
    Instant timestamp,
    UUID initiatorId
) implements ProjectEvent {

    public ProjectDeletedEvent(UUID projectId, UUID initiatorId) {
        this(
            UUID.randomUUID(),
            projectId,
            Instant.now(),
            initiatorId
        );
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public UUID getProjectId() {
        return projectId;
    }

    @Override
    public Instant getOccurredOn() {
        return timestamp;
    }
}
