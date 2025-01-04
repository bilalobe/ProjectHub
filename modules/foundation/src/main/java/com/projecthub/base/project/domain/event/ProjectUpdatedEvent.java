package com.projecthub.base.project.domain.event;

import com.projecthub.base.project.domain.entity.Project;

import java.time.Instant;
import java.util.UUID;

public record ProjectUpdatedEvent(
    UUID eventId,
    UUID projectId,
    String projectName,
    Instant timestamp,
    UUID initiatorId
) implements ProjectEvent {

    public ProjectUpdatedEvent(Project project, UUID initiatorId) {
        this(
            UUID.randomUUID(),
            project.getId(),
            project.getName(),
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
