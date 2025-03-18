package com.projecthub.base.student.domain.event;

import java.time.Instant;
import java.util.UUID;

public sealed interface StudentDomainEvent {
    UUID id();
    UUID studentId();
    Instant timestamp();

    record Created(
        UUID id,
        UUID studentId,
        UUID initiatorId,
        Instant timestamp
    ) implements StudentDomainEvent {}

    record Updated(
        UUID id,
        UUID studentId,
        UUID initiatorId,
        Instant timestamp
    ) implements StudentDomainEvent {}

    record Deleted(
        UUID id,
        UUID studentId,
        Instant timestamp
    ) implements StudentDomainEvent {}

    record Activated(
        UUID id,
        UUID studentId,
        Instant timestamp
    ) implements StudentDomainEvent {}

    record Deactivated(
        UUID id,
        UUID studentId,
        Instant timestamp
    ) implements StudentDomainEvent {}

    record TeamAssigned(
        UUID id,
        UUID studentId,
        UUID oldTeamId,
        UUID newTeamId,
        Instant timestamp
    ) implements StudentDomainEvent {}

    record EmailChanged(
        UUID id,
        UUID studentId,
        String oldEmail,
        String newEmail,
        Instant timestamp
    ) implements StudentDomainEvent {}

    record ContactUpdated(
        UUID id,
        UUID studentId,
        String phoneNumber,
        String emergencyContact,
        Instant timestamp
    ) implements StudentDomainEvent {}

    record DetailsUpdated(
        UUID id,
        UUID studentId,
        String firstName,
        String lastName,
        Instant timestamp
    ) implements StudentDomainEvent {}
}