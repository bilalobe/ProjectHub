package com.projecthub.base.project.infrastructure.event;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.event.ProjectDomainEvent;
import com.projecthub.base.project.domain.event.ProjectEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncProjectEventPublisher implements ProjectEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    @Async("projectEventExecutor")
    @Override
    public void publish(ProjectDomainEvent event) {
        log.debug("Publishing event: {}", event);
        eventPublisher.publishEvent(event);
    }

    @Async("projectEventExecutor")
    @Override
    public void publishCreated(Project project, UUID initiatorId) {
        publish(new ProjectDomainEvent.Created(project.getId(), initiatorId, Instant.now()));
    }

    @Async("projectEventExecutor")
    @Override
    public void publishUpdated(Project project, UUID initiatorId) {
        publish(new ProjectDomainEvent.Updated(project.getId(), initiatorId, Instant.now()));
    }

    @Async("projectEventExecutor")
    @Override
    public void publishDeleted(UUID projectId, UUID initiatorId) {
        publish(new ProjectDomainEvent.Deleted(projectId, initiatorId, Instant.now()));
    }

    @Async("projectEventExecutor")
    @Override
    public void publishStatusChanged(Project project, ProjectStatus oldStatus, ProjectStatus newStatus) {
        publish(new ProjectDomainEvent.StatusChanged(project.getId(), oldStatus, newStatus, Instant.now()));
    }
}
