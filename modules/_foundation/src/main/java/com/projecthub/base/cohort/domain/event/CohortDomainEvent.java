package com.projecthub.base.cohort.domain.event;

import com.projecthub.base.shared.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public sealed interface CohortDomainEvent extends DomainEvent permits
    CohortDomainEvent.Created,
    CohortDomainEvent.Updated,
    CohortDomainEvent.Deleted,
    CohortDomainEvent.Archived,
    CohortDomainEvent.StudentAdded,
    CohortDomainEvent.StudentRemoved {

    UUID cohortId();
    UUID getInitiatorId();

    record Created(
        UUID eventId,
        UUID cohortId,
        UUID initiatorId,
        String name,
        Instant occurredOn
    ) implements CohortDomainEvent {
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
        UUID cohortId,
        UUID initiatorId,
        String name,
        Instant occurredOn
    ) implements CohortDomainEvent {
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
        UUID cohortId,
        UUID initiatorId,
        Instant occurredOn
    ) implements CohortDomainEvent {
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

    record Archived(
        UUID eventId,
        UUID cohortId,
        UUID initiatorId,
        Instant occurredOn
    ) implements CohortDomainEvent {
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

    record StudentAdded(
        UUID eventId,
        UUID cohortId,
        UUID initiatorId,
        UUID studentId,
        String studentName,
        Instant occurredOn
    ) implements CohortDomainEvent {
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

    record StudentRemoved(
        UUID eventId,
        UUID cohortId,
        UUID initiatorId,
        UUID studentId,
        Instant occurredOn
    ) implements CohortDomainEvent {
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
