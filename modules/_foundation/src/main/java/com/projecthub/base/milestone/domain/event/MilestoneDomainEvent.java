package com.projecthub.base.milestone.domain.event;

import com.projecthub.base.milestone.domain.enums.MilestoneStatus;
import com.projecthub.base.shared.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public sealed interface MilestoneDomainEvent extends DomainEvent permits
    MilestoneDomainEvent.Created,
    MilestoneDomainEvent.Updated,
    MilestoneDomainEvent.Deleted,
    MilestoneDomainEvent.Completed {

    UUID milestoneId();
    UUID getInitiatorId();

    record Created(
        UUID eventId,
        UUID milestoneId,
        UUID initiatorId,
        Instant occurredOn
    ) implements MilestoneDomainEvent {
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
        UUID milestoneId,
        UUID initiatorId,
        Instant occurredOn
    ) implements MilestoneDomainEvent {
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
        UUID milestoneId,
        UUID initiatorId,
        Instant occurredOn
    ) implements MilestoneDomainEvent {
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

    record Completed(
        UUID eventId,
        UUID milestoneId,
        UUID initiatorId,
        MilestoneStatus previousStatus,
        Instant occurredOn
    ) implements MilestoneDomainEvent {
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
