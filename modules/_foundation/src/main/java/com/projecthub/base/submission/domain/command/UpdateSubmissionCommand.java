package com.projecthub.base.submission.domain.command;

import com.projecthub.base.shared.domain.enums.sync.SubmissionStatus;
import jakarta.validation.constraints.*;
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
public class UpdateSubmissionCommand {

    @NotNull(message = "Submission ID is required")
    private UUID submissionId;

    @NotBlank(message = "Content is mandatory")
    @Size(max = 5000, message = "Content must be less than 5000 characters")
    private String content;

    @Min(0L)
    @Max(100L)
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
}
