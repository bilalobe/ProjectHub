package com.projecthub.base.task.domain.event;

import com.projecthub.base.shared.events.DomainEvent;
import com.projecthub.base.task.domain.enums.TaskStatus;

import java.time.Instant;
import java.util.UUID;

public sealed interface TaskDomainEvent extends DomainEvent permits
    TaskDomainEvent.Created,
    TaskDomainEvent.Updated,
    TaskDomainEvent.Deleted,
    TaskDomainEvent.StatusChanged {

    UUID taskId();
    UUID getInitiatorId();

    record Created(
        UUID eventId,
        UUID taskId,
        UUID initiatorId,
        Instant occurredOn
    ) implements TaskDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }
    }

    record Updated(
        UUID eventId,
        UUID taskId,
        UUID initiatorId,
        Instant occurredOn
    ) implements TaskDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }
    }

    record Deleted(
        UUID eventId,
        UUID taskId,
        UUID initiatorId,
        Instant occurredOn
    ) implements TaskDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }
    }

    record StatusChanged(
        UUID eventId,
        UUID taskId,
        UUID initiatorId,
        TaskStatus oldStatus,
        TaskStatus newStatus,
        Instant occurredOn
    ) implements TaskDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }
    }
}
