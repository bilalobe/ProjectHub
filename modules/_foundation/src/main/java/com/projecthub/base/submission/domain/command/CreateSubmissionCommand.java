package com.projecthub.base.submission.domain.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
