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

    public ProjectStatusChangedEvent(Project project, ProjectStatus oldStatus, UUID initiatorId) {
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
