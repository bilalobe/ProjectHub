package com.projecthub.base.project.domain.event;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.shared.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ProjectStatusChangedEvent(
    UUID eventId,
    UUID projectId,
    String projectName,
    ProjectStatus oldStatus,
    ProjectStatus newStatus,
    Instant timestamp,
    UUID initiatorId
) implements ProjectEvent, DomainEvent {

    public ProjectStatusChangedEvent(final Project project, final ProjectStatus oldStatus, final UUID initiatorId) {
        this(
            UUID.randomUUID(),
            project.getId(),
            project.getName(),
            oldStatus,
            project.getStatus(),
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
