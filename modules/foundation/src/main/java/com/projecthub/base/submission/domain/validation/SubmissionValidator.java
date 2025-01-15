package com.projecthub.base.submission.domain.validation;

import java.io.IOException;

import com.projecthub.base.shared.exception.ValidationException;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.shared.domain.enums.sync.SubmissionStatus;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

/**
 * Validator for Submission entities. Organized in validation groups:
 * 1. Basic validations (content, fields)
 * 2. Status transitions
 * 3. Grade validations
 * 4. File path validations
 * 5. Business rules
 * 6. File size limits
 * 7. Content type validation
 * 8. Deadline validation
 * 9. Grade range pre-validation
 */
@Slf4j
@Component
public class SubmissionValidator implements SubmissionValidation {

    private static final int MAX_CONTENT_LENGTH = 5000;
    private static final int MIN_GRADE = 0;
    private static final int MAX_GRADE = 100;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final String[] ALLOWED_CONTENT_TYPES = {"application/pdf", "application/msword"};

    @Override
    public void validateCreate(@NotNull Submission submission) {
        log.debug("Validating submission creation: {}", submission.getSubmissionNumber());
        validateBasicFields(submission);
        validateInitialStatus(submission);
        validateBusinessRules(submission);
        validateFileSize(submission);
        validateContentType(submission);
        validateDeadline(submission);
    }

    @Override
    public void validateUpdate(@NotNull Submission submission) {
        log.debug("Validating submission update: {}", submission.getSubmissionNumber());
        validateBasicFields(submission);
        validateStatusTransition(submission);
        validateBusinessRules(submission);
        validateFileSize(submission);
        validateContentType(submission);
        validateDeadline(submission);
    }

    @Override
    public void validateGrade(@NotNull Submission submission) {
        log.debug("Validating submission grading: {}", submission.getSubmissionNumber());
        if (submission.getGrade() != null) {
            validateGradeValue(submission.getGrade());
        }
        validateGradingRules(submission);
    }

    @Override
    public void validateSubmit(@NotNull Submission submission) {
        log.debug("Validating submission submit: {}", submission.getSubmissionNumber());
        validateSubmissionRules(submission);
    }

    // Basic Validations
    private void validateBasicFields(@NotNull Submission submission) {
        validateContent(submission.getContent());
        validateTimestamp(submission.getTimestamp());
        validateRelationships(submission);
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new ValidationException("Content is mandatory");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new ValidationException("Content must be less than " + MAX_CONTENT_LENGTH + " characters");
        }
    }

    private void validateTimestamp(LocalDateTime timestamp) {
        if (timestamp == null) {
            throw new ValidationException("Timestamp is mandatory");
        }
        if (timestamp.isAfter(LocalDateTime.now())) {
            throw new ValidationException("Submission timestamp cannot be in the future");
        }
    }

    private void validateRelationships(@NotNull Submission submission) {
        if (submission.getStudent() == null) {
            throw new ValidationException("Student is mandatory");
        }
        if (submission.getProject() == null) {
            throw new ValidationException("Project is mandatory");
        }
    }

    // Status Validations
    private void validateInitialStatus(@NotNull Submission submission) {
        if (submission.getStatus() != SubmissionStatus.PENDING) {
            throw new ValidationException("New submissions must start in PENDING status");
        }
    }

    private void validateStatusTransition(@NotNull Submission submission) {
        if (!isValidTransition(submission.getStatus(), getTargetStatus(submission))) {
            throw new ValidationException(
                String.format("Invalid status transition from %s to %s", 
                    submission.getStatus(), getTargetStatus(submission))
            );
        }
    }

    // Grade Validations
    private void validateGradeValue(int grade) {
        if (grade < MIN_GRADE || grade > MAX_GRADE) {
            throw new ValidationException(
                String.format("Grade must be between %d and %d", MIN_GRADE, MAX_GRADE)
            );
        }
    }

    private void validateGradingRules(@NotNull Submission submission) {
        if (submission.getStatus() != SubmissionStatus.SUBMITTED) {
            throw new ValidationException("Can only grade submitted submissions");
        }
        if (submission.getFeedback() == null || submission.getFeedback().trim().isEmpty()) {
            throw new ValidationException("Feedback is required when grading");
        }
    }

    // File Path Validations
    private void validateFilePath(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!path.isAbsolute()) {
                throw new ValidationException("File path must be absolute");
            }
            // Additional file path validations can be added here
        } catch (InvalidPathException e) {
            throw new ValidationException("Invalid file path: " + e.getMessage());
        }
    }

    // Business Rules
    private void validateBusinessRules(@NotNull Submission submission) {
        validateSubmissionNumber(submission);
        if (submission.getFilePath() != null) {
            validateFilePath(submission.getFilePath());
        }
    }

    private void validateSubmissionNumber(@NotNull Submission submission) {
        if (submission.getSubmissionNumber() == null || submission.getSubmissionNumber().trim().isEmpty()) {
            throw new ValidationException("Submission number is required");
        }
        // Additional submission number format validation can be added here
    }

    private void validateSubmissionRules(@NotNull Submission submission) {
        if (submission.getStatus() != SubmissionStatus.PENDING) {
            throw new ValidationException("Can only submit pending submissions");
        }
        validateContent(submission.getContent());
        validateTimestamp(submission.getTimestamp());
    }

    private SubmissionStatus getTargetStatus(Submission submission) {
        if (submission.getGrade() != null) {
            return SubmissionStatus.GRADED;
        }
        return submission.getStatus();
    }

    private boolean isValidTransition(SubmissionStatus current, SubmissionStatus target) {
        return switch (current) {
            case PENDING -> target == SubmissionStatus.SUBMITTED;
            case SUBMITTED -> target == SubmissionStatus.GRADED;
            case GRADED -> false; // Terminal state
        };
    }

    // File Size Validation
    private void validateFileSize(@NotNull Submission submission) {
        if (submission.getFilePath() != null) {
            Path path = Paths.get(submission.getFilePath());
            try {
                long fileSize = java.nio.file.Files.size(path);
                if (fileSize > MAX_FILE_SIZE) {
                    throw new ValidationException("File size must be less than " + (MAX_FILE_SIZE / (1024 * 1024)) + " MB");
                }
            } catch (IOException e) {
                throw new ValidationException("Unable to determine file size: " + e.getMessage());
            }
        }
    }

    // Content Type Validation
    private void validateContentType(@NotNull Submission submission) {
        if (submission.getFilePath() != null) {
            Path path = Paths.get(submission.getFilePath());
            try {
                String contentType = java.nio.file.Files.probeContentType(path);
                if (!Arrays.asList(ALLOWED_CONTENT_TYPES).contains(contentType)) {
                    throw new ValidationException("Invalid file type. Allowed types are: " + String.join(", ", ALLOWED_CONTENT_TYPES));
                }
            } catch (IOException e) {
                throw new ValidationException("Unable to determine file type: " + e.getMessage());
            }
        }
    }

    // Deadline Validation
    private void validateDeadline(@NotNull Submission submission) {
        LocalDateTime deadline = submission.getProject().getDeadline();
        if (deadline != null && submission.getTimestamp().isAfter(deadline)) {
            throw new ValidationException("Submission is past the project deadline");
        }
    }
}