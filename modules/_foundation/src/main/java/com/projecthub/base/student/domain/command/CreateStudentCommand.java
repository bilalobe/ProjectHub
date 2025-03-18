package com.projecthub.base.student.domain.command;

import java.util.UUID;

public record CreateStudentCommand(
    String firstName,
    String lastName,
    String middleName,
    String email,
    String phoneNumber,
    String emergencyContact,
    UUID teamId,
    UUID initiatorId
) {
    public CreateStudentCommand {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
    }
}