package com.projecthub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Submission summary information.
 * Used for transferring submission data between processes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDTO {

    @NotNull(message = "Submission ID cannot be null")
    private UUID id;

    @NotNull(message = "Project ID cannot be null")
    private UUID projectId;

    @NotNull(message = "Student ID cannot be null")
    private UUID studentId;

    @NotBlank(message = "Content is mandatory")
    private String content;

    @NotNull(message = "Timestamp cannot be null")
    private LocalDateTime timestamp;

    private Integer grade;

    private String projectName;

    private String studentFirstName;

    private String studentLastName;
}