package com.projecthub.base.submission.api.graphql.input;

import com.projecthub.base.shared.domain.enums.sync.SubmissionStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateSubmissionInput {
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

    @NotNull(message = "Target Status is mandatory")
    private SubmissionStatus targetStatus;
}