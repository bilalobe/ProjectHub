package com.projecthub.core.dto;

import com.projecthub.core.enums.SubmissionStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Min(value = 0, message = "Grade must be at least 0")
    @Max(value = 100, message = "Grade must be at most 100")
    private Integer grade;

    @NotNull(message = "Status is mandatory")
    private SubmissionStatus status;

    @NotNull(message = "SubmittedAt cannot be null")
    private LocalDateTime submittedAt;

    private String projectName;

    private String studentFirstName;

    private String studentLastName;

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }
}