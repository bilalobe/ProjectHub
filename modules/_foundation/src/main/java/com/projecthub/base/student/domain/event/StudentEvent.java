package com.projecthub.base.student.domain.event;

import com.projecthub.base.shared.events.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public sealed interface StudentEvent extends DomainEvent {
    UUID getStudentId();
    UUID getInitiatorId();

    record StudentCreated(
        UUID eventId,
        UUID studentId,
        UUID initiatorId,
        Instant occurredOn
    ) implements StudentEvent {
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

        @Override
        public UUID getStudentId() {
            return studentId;
        }
    }

    record StudentUpdated(
        UUID eventId,
        UUID studentId,
        UUID initiatorId,
        Instant occurredOn
    ) implements StudentEvent {
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

        @Override
        public UUID getStudentId() {
            return studentId;
        }
    }

    record StudentDeleted(
        UUID eventId,
        UUID studentId,
        UUID initiatorId,
        Instant occurredOn
    ) implements StudentEvent {
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

        @Override
        public UUID getStudentId() {
            return studentId;
        }
    }
}
