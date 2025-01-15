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
    public void publish(final ProjectDomainEvent event) {
        AsyncProjectEventPublisher.log.debug("Publishing event: {}", event);
        this.eventPublisher.publishEvent(event);
    }

    @Async("projectEventExecutor")
    @Override
    public void publishCreated(final Project project, final UUID initiatorId) {
        this.publish(new ProjectDomainEvent.Created(project.getId(), initiatorId, Instant.now()));
    }

    @Async("projectEventExecutor")
    @Override
    public void publishUpdated(final Project project, final UUID initiatorId) {
        this.publish(new ProjectDomainEvent.Updated(project.getId(), initiatorId, Instant.now()));
    }

    @Async("projectEventExecutor")
    @Override
    public void publishDeleted(final UUID projectId, final UUID initiatorId) {
        this.publish(new ProjectDomainEvent.Deleted(projectId, initiatorId, Instant.now()));
    }

    @Async("projectEventExecutor")
    @Override
    public void publishStatusChanged(final Project project, final ProjectStatus oldStatus, final ProjectStatus newStatus) {
        this.publish(new ProjectDomainEvent.StatusChanged(project.getId(), oldStatus, newStatus, Instant.now()));
    }
}
