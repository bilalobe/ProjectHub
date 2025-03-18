package com.projecthub.base.submission.domain.command;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteSubmissionCommand {

    @NotNull(message = "Submission ID is required")
    private UUID submissionId;

    @NotNull(message = "Initiator ID is required")
    private UUID initiatorId;

    @NotNull(message = "Correlation ID is required")
    private UUID correlationId;

    private Map<String, String> metadata;
}