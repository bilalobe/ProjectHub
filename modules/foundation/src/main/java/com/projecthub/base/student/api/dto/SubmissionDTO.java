package com.projecthub.base.student.api.dto;

import com.projecthub.base.enums.SubmissionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing a project submission in the ProjectHub system.
 * Tracks submitted work, grades, and submission status.
 *
 * @param id               Unique identifier of the submission
 * @param projectId        Reference to the associated project
 * @param studentId        Reference to the submitting student
 * @param content          The actual submission content
 * @param timestamp        Last modification timestamp
 * @param grade            Numerical grade (0-100)
 * @param status           Current status of the submission
 * @param submittedAt      Initial submission timestamp
 * @param projectName      Name of the associated project
 * @param studentFirstName Student's given name
 * @param studentLastName  Student's family name
 */
public record SubmissionDTO(
    UUID id,
    UUID projectId,
    UUID studentId,
    String content,
    LocalDateTime timestamp,
    Integer grade,
    SubmissionStatus status,
    LocalDateTime submittedAt,
    String projectName,
    String studentFirstName,
    String studentLastName
) {
}
