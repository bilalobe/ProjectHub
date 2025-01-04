package com.projecthub.base.task.domain.event;

import com.projecthub.base.task.domain.entity.Task;

import java.time.Instant;
import java.util.UUID;

public interface TaskEventPublisher {
    void publish(TaskDomainEvent event);

    default void publishCreated(Task task, UUID initiatorId) {
        publish(new TaskDomainEvent.Created(
            UUID.randomUUID(),
            task.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    default void publishUpdated(Task task, UUID initiatorId) {
        publish(new TaskDomainEvent.Updated(
            UUID.randomUUID(),
            task.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    default void publishDeleted(UUID taskId, UUID initiatorId) {
        publish(new TaskDomainEvent.Deleted(
            UUID.randomUUID(),
            taskId,
            initiatorId,
            Instant.now()
        ));
    }
}
