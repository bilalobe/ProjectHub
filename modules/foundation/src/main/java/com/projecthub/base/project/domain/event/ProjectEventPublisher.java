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

    public void publishCreated(Project project, UUID initiatorId) {
        var event = new ProjectCreatedEvent(project, initiatorId);
        log.debug("Publishing project created event for project: {}", project.getId());
        publisher.publishEvent(event);
    }

    public void publishUpdated(Project project, UUID initiatorId) {
        var event = new ProjectUpdatedEvent(project, initiatorId);
        log.debug("Publishing project updated event for project: {}", project.getId());
        publisher.publishEvent(event);
    }

    public void publishDeleted(UUID projectId, UUID initiatorId) {
        var event = new ProjectDeletedEvent(projectId, initiatorId);
        log.debug("Publishing project deleted event for project: {}", projectId);
        publisher.publishEvent(event);
    }

    public void publishStatusChanged(Project project, ProjectStatus oldStatus, UUID initiatorId) {
        var event = new ProjectStatusChangedEvent(project, oldStatus, initiatorId);
        log.debug("Publishing project status changed event for project: {} from {} to {}",
            project.getId(), oldStatus, project.getStatus());
        publisher.publishEvent(event);
    }
}
