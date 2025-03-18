package com.projecthub.base.submission.domain.validation;

import com.projecthub.base.shared.exception.ValidationException;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.domain.repository.SubmissionJpaRepository;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NonNls;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubmissionValidator {
    private final SubmissionJpaRepository repository;
    private final Validator validator;

    public void validateCreate(Submission submission) {
        var violations = validator.validate(submission);
        if (!violations.isEmpty()) {
            throw new ValidationException("Invalid submission data", violations);
        }

        if (repository.hasValidSubmissionForProject(submission.getStudentId(), submission.getProjectId())) {
            throw new ValidationException("Student already has a valid submission for this project");
        }
    }

    public void validateUpdate(Submission submission, UUID id) {
        var violations = validator.validate(submission);
        if (!violations.isEmpty()) {
            throw new ValidationException("Invalid submission data", violations);
        }

        repository.findBySubmissionId(id)
            .filter(existing -> existing.getStudentId().equals(submission.getStudentId()))
            .orElseThrow(() -> new ValidationException("Cannot update submission: not found or unauthorized"));
    }

    public void validateSubmit(Submission submission) {
        if (submission.getContent() == null || submission.getContent().trim().isEmpty()) {
            throw new ValidationException("Cannot submit empty submission");
        }
    }

    public void validateGrade(int grade, @NonNls String feedback) {
        if (grade < 0 || grade > 100) {
            throw new ValidationException("Grade must be between 0 and 100");
        }
        if (feedback == null || feedback.trim().isEmpty()) {
            throw new ValidationException("Feedback cannot be empty");
        }
    }

    public void validateComment(@NonNls String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new ValidationException("Comment cannot be empty");
        }
        if (comment.length() > 1000) {
            throw new ValidationException("Comment cannot exceed 1000 characters");
        }
    }
}
