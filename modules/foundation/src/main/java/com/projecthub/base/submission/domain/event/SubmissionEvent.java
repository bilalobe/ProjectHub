package com.projecthub.base.submission.domain.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public sealed interface SubmissionEvent permits SubmissionEvent.SubmissionCreated, SubmissionEvent.SubmissionUpdated, SubmissionEvent.SubmissionDeleted {

    UUID getEventId();
    UUID getSubmissionId();
    UUID getInitiatorId();
    Instant getOccurredOn();

    String EVENT_ID_NULL = "Event ID cannot be null";
    String SUBMISSION_ID_NULL = "Submission ID cannot be null";
    String INITIATOR_ID_NULL = "Initiator ID cannot be null";
    String OCCURRED_ON_NULL = "Occurred On cannot be null";

    record SubmissionCreated(
        UUID eventId,
        UUID submissionId,
        UUID initiatorId,
        Instant occurredOn
    ) implements SubmissionEvent {
        public SubmissionCreated {
            Objects.requireNonNull(eventId, EVENT_ID_NULL);
            Objects.requireNonNull(submissionId, SUBMISSION_ID_NULL);
            Objects.requireNonNull(initiatorId, INITIATOR_ID_NULL);
            Objects.requireNonNull(occurredOn, OCCURRED_ON_NULL);
        }

        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public UUID getSubmissionId() {
            return submissionId;
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

    record SubmissionUpdated(
        UUID eventId,
        UUID submissionId,
        UUID initiatorId,
        Instant occurredOn
    ) implements SubmissionEvent {
        public SubmissionUpdated {
            Objects.requireNonNull(eventId, EVENT_ID_NULL);
            Objects.requireNonNull(submissionId, SUBMISSION_ID_NULL);
            Objects.requireNonNull(initiatorId, INITIATOR_ID_NULL);
            Objects.requireNonNull(occurredOn, OCCURRED_ON_NULL);
        }

        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public UUID getSubmissionId() {
            return submissionId;
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

    record SubmissionDeleted(
        UUID eventId,
        UUID submissionId,
        UUID initiatorId,
        Instant occurredOn
    ) implements SubmissionEvent {
        public SubmissionDeleted {
            Objects.requireNonNull(eventId, EVENT_ID_NULL);
            Objects.requireNonNull(submissionId, SUBMISSION_ID_NULL);
            Objects.requireNonNull(initiatorId, INITIATOR_ID_NULL);
            Objects.requireNonNull(occurredOn, OCCURRED_ON_NULL);
        }

        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public UUID getSubmissionId() {
            return submissionId;
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
}