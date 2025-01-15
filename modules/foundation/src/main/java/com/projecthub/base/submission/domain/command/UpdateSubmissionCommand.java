package com.projecthub.base.submission.domain.command;

import com.projecthub.base.shared.domain.enums.sync.SubmissionStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
public class UpdateSubmissionCommand {

    @NotNull(message = "Submission ID is required")
    private UUID submissionId;

    @NotBlank(message = "Content is mandatory")
    @Size(max = 5000, message = "Content must be less than 5000 characters")
    private String content;

    @Min(0)
    @Max(100)
    private Integer grade;

    @Size(max = 1000)
    private String feedback;

    private String reviewerNotes;

    private String filePath;

    @NotNull(message = "Target status is required")
    private SubmissionStatus targetStatus;

    @NotNull(message = "Initiator ID is required")
    private UUID initiatorId;

    @NotNull(message = "Correlation ID is required")
    private UUID correlationId;

    private Map<String, String> metadata;

    public UpdateSubmissionCommand {
        Objects.requireNonNull(submissionId, "Submission ID cannot be null");
        Objects.requireNonNull(content, "Content cannot be null");
        Objects.requireNonNull(targetStatus, "Target Status cannot be null");
        Objects.requireNonNull(initiatorId, "Initiator ID cannot be null");
        Objects.requireNonNull(correlationId, "Correlation ID cannot be null");
    }
}