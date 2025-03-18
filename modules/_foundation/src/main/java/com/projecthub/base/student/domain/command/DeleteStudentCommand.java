package com.projecthub.base.student.domain.command;

import java.util.UUID;

public record DeleteStudentCommand(
    UUID id,
    UUID initiatorId,
    String reason
) {
    public DeleteStudentCommand {
        if (id == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        if (initiatorId == null) {
            throw new IllegalArgumentException("Initiator ID cannot be null");
        }
    }
}