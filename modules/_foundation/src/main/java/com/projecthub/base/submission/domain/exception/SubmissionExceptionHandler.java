package com.projecthub.base.submission.domain.exception;

import com.projecthub.base.shared.exception.ErrorResponse;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class SubmissionExceptionHandler {

    public SubmissionExceptionHandler() {
    }

    @ExceptionHandler(OptimisticLockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleConcurrentModification(OptimisticLockException ex) {
        log.error("Concurrent modification detected", ex);
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Resource was modified by another user",
                ex.getMessage());
    }

    @ExceptionHandler(DeadlineViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleDeadlineViolation(DeadlineViolationException ex) {
        log.error("Deadline violation", ex);
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Submission deadline has passed",
                ex.getMessage());
    }

    @ExceptionHandler(FileHandlingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleFileError(FileHandlingException ex) {
        log.error("File handling error", ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error processing submission file",
                ex.getMessage());
    }

    @ExceptionHandler(GradeValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleGradeValidation(GradeValidationException ex) {
        log.error("Grade validation error", ex);
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid grade value",
                ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access denied",
                ex.getMessage());
    }

    @ExceptionHandler(SubmissionSecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleSecurityException(SubmissionSecurityException ex) {
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Security violation",
                ex.getMessage());
    }

    private static ResponseEntity<ErrorResponse> buildErrorResponse(
        HttpStatus status, String message, String detail) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .code("SUBMISSION_" + status.value())
                .message(message)
                .detail(detail)
                .error(status.getReasonPhrase())
                .build();

        log.error("Error Response: [Status: {}, Code: {}, Message: {}, RequestId: {}]",
                Integer.valueOf(error.status()),
                error.code(),
                error.message(),
                error.requestId());

        return new ResponseEntity<>(error, status);
    }
}
