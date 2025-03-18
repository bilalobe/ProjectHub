package com.projecthub.base.student.api.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object representing a student in the ProjectHub system.
 * Contains basic student information and team association.
 *
 * @param id                Unique identifier of the student
 * @param email             Student's contact email address
 * @param firstName         Student's given name
 * @param lastName          Student's family name
 * @param middleName        Student's middle name
 * @param phoneNumber       Student's contact phone number
 * @param emergencyContact  Student's emergency contact information
 * @param teamId            Reference to the team the student belongs to (optional)
 * @param teamName          Name of the team the student belongs to
 * @param enrollmentDate    Date when the student enrolled
 * @param status            Current status of the student
 */
public record StudentDTO(
    UUID id,
    String firstName,
    String lastName,
    String middleName,
    String email,
    String phoneNumber,
    String emergencyContact,
    UUID teamId,
    String teamName,
    LocalDate enrollmentDate,
    String status
) {}
