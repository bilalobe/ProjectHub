package com.projecthub.base.project.domain.event;

import com.projecthub.base.project.domain.entity.Project;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ProjectUpdatedEvent(
    UUID eventId,
    UUID projectId,
    String projectName,
    Instant timestamp,
    UUID initiatorId,
    Map<String, Object> updatedFields
) implements ProjectEvent {

    public ProjectUpdatedEvent(final Project project, final UUID initiatorId, final Map<String, Object> updatedFields) {
        this(
            UUID.randomUUID(),
            project.getId(),
            project.getName(),
            Instant.now(),
            initiatorId,
            updatedFields
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

    @Override
    public String getProjectName() {
        return projectName();
    }
}
