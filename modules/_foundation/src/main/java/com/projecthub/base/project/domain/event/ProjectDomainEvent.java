package com.projecthub.base.project.domain.event;

import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.shared.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public sealed interface ProjectDomainEvent extends DomainEvent permits
    ProjectDomainEvent.Created,
    ProjectDomainEvent.Updated,
    ProjectDomainEvent.Deleted,
    ProjectDomainEvent.StatusChanged {

    UUID projectId();
    UUID getInitiatorId();

    record Created(
        UUID eventId,
        UUID projectId, 
        UUID initiatorId,
        Instant occurredOn
    ) implements ProjectDomainEvent {
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
        UUID projectId, 
        UUID initiatorId,
        Instant occurredOn
    ) implements ProjectDomainEvent {
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
        UUID projectId, 
        UUID initiatorId,
        Instant occurredOn
    ) implements ProjectDomainEvent {
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
        UUID projectId,
        UUID initiatorId,
        ProjectStatus oldStatus,
        ProjectStatus newStatus,
        Instant occurredOn
    ) implements ProjectDomainEvent {
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
