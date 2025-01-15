package com.projecthub.base.school.domain.event;

import com.projecthub.base.school.domain.value.SchoolContact;

import java.time.Instant;
import java.util.UUID;

public sealed interface SchoolDomainEvent {
    UUID getEventId();

    UUID getSchoolId();

    UUID getInitiatorId();

    Instant getOccurredOn();

    record Created(
        UUID eventId,
        UUID schoolId,
        UUID initiatorId,
        Instant occurredOn
    ) implements SchoolDomainEvent {

        @Override
        public UUID getEventId() {
            return this.eventId;
        }

        @Override
        public UUID getSchoolId() {
            return this.schoolId;
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
        UUID schoolId,
        UUID initiatorId,
        Instant occurredOn
    ) implements SchoolDomainEvent {

        @Override
        public UUID getEventId() {
            return this.eventId;
        }

        @Override
        public UUID getSchoolId() {
            return this.schoolId;
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
        UUID schoolId,
        UUID initiatorId,
        Instant occurredOn
    ) implements SchoolDomainEvent {

        @Override
        public UUID getEventId() {
            return this.eventId;
        }

        @Override
        public UUID getSchoolId() {
            return this.schoolId;
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
        UUID schoolId,
        UUID initiatorId,
        String reason,
        Instant occurredOn
    ) implements SchoolDomainEvent {

        @Override
        public UUID getEventId() {
            return this.eventId;
        }

        @Override
        public UUID getSchoolId() {
            return this.schoolId;
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

    record CohortAdded(
        UUID eventId,
        UUID schoolId,
        UUID cohortId,
        UUID initiatorId,
        Instant occurredOn
    ) implements SchoolDomainEvent {

        @Override
        public UUID getEventId() {
            return this.eventId;
        }

        @Override
        public UUID getSchoolId() {
            return this.schoolId;
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

    record NameUpdated(
        UUID eventId,
        UUID schoolId,
        String oldName,
        String newName,
        UUID initiatorId,
        Instant occurredOn
    ) implements SchoolDomainEvent {
        @Override
        public UUID getEventId() {
            return this.eventId;
        }

        @Override
        public UUID getSchoolId() {
            return this.schoolId;
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

    record ContactInfoUpdated(
        UUID eventId,
        UUID schoolId,
        SchoolContact oldContact,
        SchoolContact newContact,
        UUID initiatorId,
        Instant occurredOn
    ) implements SchoolDomainEvent {
        @Override
        public UUID getEventId() {
            return this.eventId;
        }

        @Override
        public UUID getSchoolId() {
            return this.schoolId;
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
