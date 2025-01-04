package com.projecthub.base.student.domain.event;

import java.util.UUID;

public sealed interface StudentEvent {

    record StudentCreated(UUID studentId) implements StudentEvent {
    }

    record StudentUpdated(UUID studentId) implements StudentEvent {
    }

    record StudentDeleted(UUID studentId) implements StudentEvent {
    }
}
