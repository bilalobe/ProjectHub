package com.projecthub.base.cohort.domain.event;

import java.time.Instant;
import java.util.UUID;

public sealed interface CohortDomainEvent {
    UUID getEventId();

    UUID getCohortId();

    UUID getInitiatorId();

    Instant getOccurredOn();

    record Created(
        UUID eventId,
        UUID cohortId,
        UUID schoolId,
        String name,
        int maxStudents,
        UUID initiatorId,
        Instant occurredOn
    ) implements CohortDomainEvent {
        @Override
        public UUID getEventId() {
            return this.eventId;
        }

        @Override
        public UUID getCohortId() {
            return this.cohortId;
        }

        @Override
        public UUID getInitiatorId() {
            return this.initiatorId;
        }

        @Override
        public Instant getOccurredOn() {
            return this.occurredOn;
        }
    }

    record Updated(
        UUID eventId,
        UUID cohortId,
        String name,
        int maxStudents,
        UUID initiatorId,
        Instant occurredOn
    ) implements CohortDomainEvent {
        @Override
        public UUID getEventId() {
            return this.eventId;
        }

        @Override
        public UUID getCohortId() {
            return this.cohortId;
        }

        @Override
        public UUID getInitiatorId() {
            return this.initiatorId;
        }

        @Override
        public Instant getOccurredOn() {
            return this.occurredOn;
        }
    }

    record Archived(
        UUID eventId,
        UUID cohortId,
        String reason,
        UUID initiatorId,
        Instant occurredOn
    ) implements CohortDomainEvent {
        @Override
        public UUID getEventId() {
            return this.eventId;
        }

        @Override
        public UUID getCohortId() {
            return this.cohortId;
        }

        @Override
        public UUID getInitiatorId() {
            return this.initiatorId;
        }

        @Override
        public Instant getOccurredOn() {
            return this.occurredOn;
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
            return this.eventId;
        }

        @Override
        public UUID getCohortId() {
            return this.cohortId;
        }

        @Override
        public UUID getInitiatorId() {
            return this.initiatorId;
        }

        @Override
        public Instant getOccurredOn() {
            return this.occurredOn;
        }
    }

    record StudentAdded(
        UUID eventId,
        UUID cohortId,
        UUID studentId,
        UUID initiatorId,
        Instant occurredOn
    ) implements CohortDomainEvent {
        @Override
        public UUID getEventId() {
            return this.eventId;
        }

        @Override
        public UUID getCohortId() {
            return this.cohortId;
        }

        @Override
        public UUID getInitiatorId() {
            return this.initiatorId;
        }

        @Override
        public Instant getOccurredOn() {
            return this.occurredOn;
        }
    }

    record StudentRemoved(
        UUID eventId,
        UUID cohortId,
        UUID studentId,
        String reason,
        UUID initiatorId,
        Instant occurredOn
    ) implements CohortDomainEvent {
        @Override
        public UUID getEventId() {
            return this.eventId;
        }

        @Override
        public UUID getCohortId() {
            return this.cohortId;
        }

        @Override
        public UUID getInitiatorId() {
            return this.initiatorId;
        }

        @Override
        public Instant getOccurredOn() {
            return this.occurredOn;
        }
    }
}
