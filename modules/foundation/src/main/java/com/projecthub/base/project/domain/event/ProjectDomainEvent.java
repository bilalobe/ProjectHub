package com.projecthub.base.project.domain.event;

import com.projecthub.base.project.domain.enums.ProjectStatus;

import java.time.Instant;
import java.util.UUID;

public sealed interface ProjectDomainEvent permits
    ProjectDomainEvent.Created,
    ProjectDomainEvent.Updated,
    ProjectDomainEvent.Deleted,
    ProjectDomainEvent.StatusChanged {

    UUID projectId();

    Instant timestamp();

    record Created(UUID projectId, UUID initiatorId, Instant timestamp) implements ProjectDomainEvent {
    }

    record Updated(UUID projectId, UUID initiatorId, Instant timestamp) implements ProjectDomainEvent {
    }

    record Deleted(UUID projectId, UUID initiatorId, Instant timestamp) implements ProjectDomainEvent {
    }

    record StatusChanged(UUID projectId, ProjectStatus oldStatus, ProjectStatus newStatus, Instant timestamp)
        implements ProjectDomainEvent {
    }
}
