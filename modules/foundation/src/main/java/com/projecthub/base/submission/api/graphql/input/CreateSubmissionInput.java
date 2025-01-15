package com.projecthub.base.submission.api.graphql.input;

import com.projecthub.base.shared.domain.enums.sync.SubmissionStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateSubmissionInput {

     @NotBlank(message = "Content is mandatory")
    @Size(max = 5000, message = "Content must be less than 5000 characters")
    private String content;

   @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotNull(message = "Project ID is required")
    private UUID projectId;

    private String filePath;
}