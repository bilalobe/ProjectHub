package com.projecthub.base.submission.domain.validation;

import com.projecthub.base.submission.domain.entity.Submission;
import jakarta.validation.constraints.NotNull;

public interface SubmissionValidation {
    void validateCreate(@NotNull Submission submission);
    void validateUpdate(@NotNull Submission submission);
    void validateGrade(@NotNull Submission submission);
    void validateSubmit(@NotNull Submission submission);
}