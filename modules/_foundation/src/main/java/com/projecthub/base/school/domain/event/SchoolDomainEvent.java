package com.projecthub.base.school.domain.event;

import com.projecthub.base.school.domain.enums.SchoolType;
import com.projecthub.base.school.domain.value.SchoolContact;
import com.projecthub.base.shared.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public sealed interface SchoolDomainEvent extends DomainEvent permits
    SchoolDomainEvent.Created,
    SchoolDomainEvent.Updated,
    SchoolDomainEvent.Deleted,
    SchoolDomainEvent.TypeChanged,
    SchoolDomainEvent.CohortAdded,
    SchoolDomainEvent.NameUpdated,
    SchoolDomainEvent.ContactInfoUpdated {

    UUID getSchoolId();
    UUID getInitiatorId();

    record Created(
        UUID eventId,
        UUID schoolId,
        UUID initiatorId,
        String name,
        SchoolType type,
        Instant occurredOn
    ) implements SchoolDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public UUID getSchoolId() {
            return schoolId;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }
    }

    record Updated(
        UUID eventId,
        UUID schoolId,
        UUID initiatorId,
        String name,
        Instant occurredOn
    ) implements SchoolDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public UUID getSchoolId() {
            return schoolId;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
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
            return eventId;
        }

        @Override
        public UUID getSchoolId() {
            return schoolId;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }
    }

    record TypeChanged(
        UUID eventId,
        UUID schoolId,
        UUID initiatorId,
        SchoolType oldType,
        SchoolType newType,
        Instant occurredOn
    ) implements SchoolDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public UUID getSchoolId() {
            return schoolId;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }
    }

    record CohortAdded(
        UUID eventId,
        UUID schoolId,
        UUID cohortId,
        UUID initiatorId,
        String cohortName,
        Instant occurredOn
    ) implements SchoolDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public UUID getSchoolId() {
            return schoolId;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
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
