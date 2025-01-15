package com.projecthub.base.project.domain.event;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publishCreated(final Project project, final UUID initiatorId) {
        final var event = new ProjectCreatedEvent(project, initiatorId);
        ProjectEventPublisher.log.debug("Publishing project created event for project: {}", project.getId());
        this.publisher.publishEvent(event);
    }

    public void publishUpdated(final Project project, final UUID initiatorId) {
        final var event = new ProjectUpdatedEvent(project, initiatorId);
        ProjectEventPublisher.log.debug("Publishing project updated event for project: {}", project.getId());
        this.publisher.publishEvent(event);
    }

    public void publishDeleted(final UUID projectId, final UUID initiatorId) {
        final var event = new ProjectDeletedEvent(projectId, initiatorId);
        ProjectEventPublisher.log.debug("Publishing project deleted event for project: {}", projectId);
        this.publisher.publishEvent(event);
    }

    public void publishStatusChanged(final Project project, final ProjectStatus oldStatus, final UUID initiatorId) {
        final var event = new ProjectStatusChangedEvent(project, oldStatus, initiatorId);
        ProjectEventPublisher.log.debug("Publishing project status changed event for project: {} from {} to {}",
            project.getId(), oldStatus, project.getStatus());
        this.publisher.publishEvent(event);
    }
}
