package com.projecthub.base.submission.domain.event;

import com.projecthub.base.shared.events.DomainEvent;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public sealed interface SubmissionEvent extends DomainEvent permits 
    SubmissionEvent.SubmissionCreated, 
    SubmissionEvent.SubmissionUpdated, 
    SubmissionEvent.SubmissionDeleted {

    UUID getSubmissionId();
    UUID getInitiatorId();

    record SubmissionCreated(
        UUID eventId,
        UUID submissionId,
        UUID initiatorId,
        Instant occurredOn
    ) implements SubmissionEvent {
        public SubmissionCreated {
            Objects.requireNonNull(eventId, "Event ID cannot be null");
            Objects.requireNonNull(submissionId, "Submission ID cannot be null");
            Objects.requireNonNull(initiatorId, "Initiator ID cannot be null");
            Objects.requireNonNull(occurredOn, "Occurred On cannot be null");
        }

        @Override
        public UUID getEventId() {
            return eventId;
        }
    }

    record SubmissionUpdated(
        UUID eventId,
        UUID submissionId,
        UUID initiatorId,
        Instant occurredOn
    ) implements SubmissionEvent {
        public SubmissionUpdated {
            Objects.requireNonNull(eventId, "Event ID cannot be null");
            Objects.requireNonNull(submissionId, "Submission ID cannot be null");
            Objects.requireNonNull(initiatorId, "Initiator ID cannot be null");
            Objects.requireNonNull(occurredOn, "Occurred On cannot be null");
        }

        @Override
        public UUID getEventId() {
            return eventId;
        }
    }

    record SubmissionDeleted(
        UUID eventId,
        UUID submissionId,
        UUID initiatorId,
        Instant occurredOn
    ) implements SubmissionEvent {
        public SubmissionDeleted {
            Objects.requireNonNull(eventId, "Event ID cannot be null");
            Objects.requireNonNull(submissionId, "Submission ID cannot be null");
            Objects.requireNonNull(initiatorId, "Initiator ID cannot be null");
            Objects.requireNonNull(occurredOn, "Occurred On cannot be null");
        }

        @Override
        public UUID getEventId() {
            return eventId;
        }
    }
}