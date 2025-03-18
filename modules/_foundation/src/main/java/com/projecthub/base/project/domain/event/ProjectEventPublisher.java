package com.projecthub.base.project.domain.event;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.shared.infrastructure.event.DomainEventPublisher;

import java.util.UUID;

/**
 * Publisher interface for project domain events.
 */
public interface ProjectEventPublisher extends DomainEventPublisher<ProjectDomainEvent> {
    default void publishCreated(Project project, UUID initiatorId) {
        publish(new ProjectDomainEvent.Created(
            generateEventId(),
            project.getId(),
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishUpdated(Project project, UUID initiatorId) {
        publish(new ProjectDomainEvent.Updated(
            generateEventId(),
            project.getId(),
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishDeleted(UUID projectId, UUID initiatorId) {
        publish(new ProjectDomainEvent.Deleted(
            generateEventId(),
            projectId,
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishStatusChanged(Project project, ProjectStatus oldStatus, ProjectStatus newStatus, UUID initiatorId) {
        publish(new ProjectDomainEvent.StatusChanged(
            generateEventId(),
            project.getId(),
            initiatorId,
            oldStatus,
            newStatus,
            getTimestamp()
        ));
    }
}
