package com.projecthub.base.component.domain.event;

import com.projecthub.base.shared.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public sealed interface ComponentDomainEvent extends DomainEvent permits
    ComponentDomainEvent.Created,
    ComponentDomainEvent.Updated,
    ComponentDomainEvent.Deleted {

    UUID componentId();
    UUID getInitiatorId();

    record Created(
        UUID eventId,
        UUID componentId,
        UUID initiatorId,
        Instant occurredOn
    ) implements ComponentDomainEvent {
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
        UUID componentId,
        UUID initiatorId,
        Instant occurredOn
    ) implements ComponentDomainEvent {
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
        UUID componentId,
        UUID initiatorId,
        Instant occurredOn
    ) implements ComponentDomainEvent {
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
