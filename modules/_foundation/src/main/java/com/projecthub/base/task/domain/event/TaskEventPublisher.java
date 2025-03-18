package com.projecthub.base.task.domain.event;

import com.projecthub.base.shared.infrastructure.event.DomainEventPublisher;
import com.projecthub.base.task.domain.entity.Task;
import com.projecthub.base.task.domain.enums.TaskStatus;

import java.util.UUID;

/**
 * Publisher interface for task domain events.
 */
public interface TaskEventPublisher extends DomainEventPublisher<TaskDomainEvent> {
    default void publishCreated(Task task, UUID initiatorId) {
        publish(new TaskDomainEvent.Created(
            generateEventId(),
            task.getId(),
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishUpdated(Task task, UUID initiatorId) {
        publish(new TaskDomainEvent.Updated(
            generateEventId(),
            task.getId(),
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishDeleted(UUID taskId, UUID initiatorId) {
        publish(new TaskDomainEvent.Deleted(
            generateEventId(),
            taskId,
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishStatusChanged(Task task, TaskStatus oldStatus, UUID initiatorId) {
        publish(new TaskDomainEvent.StatusChanged(
            generateEventId(),
            task.getId(),
            initiatorId,
            oldStatus,
            task.getStatus(),
            getTimestamp()
        ));
    }
}
