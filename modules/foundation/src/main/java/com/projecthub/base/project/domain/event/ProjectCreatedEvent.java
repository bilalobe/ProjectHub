package com.projecthub.base.project.domain.event;

import com.projecthub.base.project.domain.entity.Project;

import java.time.Instant;
import java.util.UUID;

public record ProjectCreatedEvent(
    UUID eventId,
    UUID projectId,
    String projectName,
    UUID teamId,
    Instant timestamp,
    UUID initiatorId
) implements ProjectEvent {

    public ProjectCreatedEvent(final Project project, final UUID initiatorId) {
        this(
            UUID.randomUUID(),
            project.getId(),
            project.getName(),
            project.getTeam().teamId(),
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
