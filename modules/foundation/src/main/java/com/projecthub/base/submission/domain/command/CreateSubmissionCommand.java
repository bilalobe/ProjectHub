package com.projecthub.base.submission.domain.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
public class CreateSubmissionCommand {

    @NotBlank(message = "Content is mandatory")
    @Size(max = 5000, message = "Content must be less than 5000 characters")
    private String content;

    @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotNull(message = "Project ID is required")
    private UUID projectId;

    private LocalDateTime timestamp;

    private String filePath;

    @NotNull(message = "Initiator ID is required")
    private UUID initiatorId;

    @NotNull(message = "Correlation ID is required")
    private UUID correlationId;

    private LocalDateTime deadline;

    private Map<String, String> metadata;

    public CreateSubmissionCommand {
        Objects.requireNonNull(content, "Content cannot be null");
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        Objects.requireNonNull(projectId, "Project ID cannot be null");
        Objects.requireNonNull(initiatorId, "Initiator ID cannot be null");
        Objects.requireNonNull(correlationId, "Correlation ID cannot be null");
    }
}