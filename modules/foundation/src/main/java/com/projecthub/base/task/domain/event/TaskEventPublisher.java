package com.projecthub.base.task.domain.event;

import com.projecthub.base.task.domain.entity.Task;

import java.time.Instant;
import java.util.UUID;

public interface TaskEventPublisher {
    void publish(TaskDomainEvent event);

    default void publishCreated(final Task task, final UUID initiatorId) {
        this.publish(new TaskDomainEvent.Created(
            UUID.randomUUID(),
            task.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    default void publishUpdated(final Task task, final UUID initiatorId) {
        this.publish(new TaskDomainEvent.Updated(
            UUID.randomUUID(),
            task.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    default void publishDeleted(final UUID taskId, final UUID initiatorId) {
        this.publish(new TaskDomainEvent.Deleted(
            UUID.randomUUID(),
            taskId,
            initiatorId,
            Instant.now()
        ));
    }
}
