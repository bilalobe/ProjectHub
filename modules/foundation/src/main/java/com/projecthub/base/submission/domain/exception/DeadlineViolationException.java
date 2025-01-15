package com.projecthub.base.submission.domain.exception;

public class DeadlineViolationException extends RuntimeException {
    public DeadlineViolationException(String message) {
        super(message);
    }
}
