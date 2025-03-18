package com.projecthub.base.submission.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SubmissionExceptionHandler {

    public SubmissionExceptionHandler() {
    }

    @ExceptionHandler(SubmissionValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(SubmissionValidationException ex) {
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(SubmissionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(SubmissionNotFoundException ex) {
        return ResponseEntity
            .notFound()
            .build();
    }
}
