package com.projecthub.base.submission.domain.command;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
public class DeleteSubmissionCommand {

    @NotNull(message = "Submission ID is required")
    private UUID submissionId;

    @NotNull(message = "Initiator ID is required")
    private UUID initiatorId;

    @NotNull(message = "Correlation ID is required")
    private UUID correlationId;

    private Map<String, String> metadata;

    public DeleteSubmissionCommand {
        Objects.requireNonNull(submissionId, "Submission ID cannot be null");
        Objects.requireNonNull(initiatorId, "Initiator ID cannot be null");
        Objects.requireNonNull(correlationId, "Correlation ID cannot be null");
    }
}