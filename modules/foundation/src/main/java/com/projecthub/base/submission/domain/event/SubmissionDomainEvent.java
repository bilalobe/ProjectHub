package com.projecthub.base.submission.domain.event;

import com.projecthub.base.shared.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public sealed interface SubmissionDomainEvent extends DomainEvent {
    UUID getEventId();
    UUID getSubmissionId();
    Instant getTimestamp();

    record Created(
        UUID eventId,
        UUID submissionId, 
        String submissionNumber,
        UUID studentId,
        UUID projectId,
        Instant timestamp
    ) implements SubmissionDomainEvent {
        public Created {
            Objects.requireNonNull(eventId, "Event ID cannot be null");
            Objects.requireNonNull(submissionId, "Submission ID cannot be null");
            Objects.requireNonNull(submissionNumber, "Submission number cannot be null");
            Objects.requireNonNull(studentId, "Student ID cannot be null");
            Objects.requireNonNull(projectId, "Project ID cannot be null");
            Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        }
    }

    record Submitted(
        UUID eventId,
        UUID submissionId,
        String submissionNumber,
        Instant timestamp
    ) implements SubmissionDomainEvent {
        public Submitted {
            Objects.requireNonNull(eventId, "Event ID cannot be null");
            Objects.requireNonNull(submissionId, "Submission ID cannot be null");
            Objects.requireNonNull(submissionNumber, "Submission number cannot be null");
            Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        }
    }

    record Graded(
        UUID eventId,
        UUID submissionId,
        String submissionNumber,
        int grade,
        String feedback,
        Instant timestamp
    ) implements SubmissionDomainEvent {
        public Graded {
            Objects.requireNonNull(eventId, "Event ID cannot be null");
            Objects.requireNonNull(submissionId, "Submission ID cannot be null");
            Objects.requireNonNull(submissionNumber, "Submission number cannot be null");
            Objects.requireNonNull(feedback, "Feedback cannot be null");
            Objects.requireNonNull(timestamp, "Timestamp cannot be null");
            if (grade < 0 || grade > 100) {
                throw new IllegalArgumentException("Grade must be between 0 and 100");
            }
        }
    }

    @Override
    default Instant getTimestamp() {
        return timestamp;
    }
}