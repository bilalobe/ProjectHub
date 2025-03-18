package com.projecthub.base.team.domain.event;

import com.projecthub.base.shared.events.DomainEvent;
import com.projecthub.base.team.domain.enums.TeamStatus;

import java.time.Instant;
import java.util.UUID;

public sealed interface TeamDomainEvent extends DomainEvent permits
    TeamDomainEvent.Created,
    TeamDomainEvent.Updated,
    TeamDomainEvent.Deleted,
    TeamDomainEvent.StatusChanged,
    TeamDomainEvent.MemberAdded,
    TeamDomainEvent.MemberRemoved,
    TeamDomainEvent.AssignedToCohort {

    UUID teamId();
    UUID getInitiatorId();

    record Created(
        UUID eventId,
        UUID teamId,
        UUID initiatorId,
        String name,
        Instant occurredOn
    ) implements TeamDomainEvent {
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
        UUID teamId,
        UUID initiatorId,
        String name,
        Instant occurredOn
    ) implements TeamDomainEvent {
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
        UUID teamId,
        UUID initiatorId,
        Instant occurredOn
    ) implements TeamDomainEvent {
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
        UUID teamId,
        UUID initiatorId,
        TeamStatus oldStatus,
        TeamStatus newStatus,
        Instant occurredOn
    ) implements TeamDomainEvent {
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

    record MemberAdded(
        UUID eventId,
        UUID teamId,
        UUID initiatorId,
        UUID memberId,
        String memberName,
        Instant occurredOn
    ) implements TeamDomainEvent {
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

    record MemberRemoved(
        UUID eventId,
        UUID teamId,
        UUID initiatorId,
        UUID memberId,
        Instant occurredOn
    ) implements TeamDomainEvent {
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

    record AssignedToCohort(
        UUID eventId,
        UUID teamId,
        UUID initiatorId,
        UUID cohortId,
        Instant occurredOn
    ) implements TeamDomainEvent {
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