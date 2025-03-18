package com.projecthub.base.submission.api.graphql.input;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GradeSubmissionInput {
    @Min(0L)
    @Max(100L)
    private int grade;

    @NotBlank
    @Size(max = 1000)
    private String feedback;
}
