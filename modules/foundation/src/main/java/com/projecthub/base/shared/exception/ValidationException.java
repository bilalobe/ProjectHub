package com.projecthub.base.shared.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends BaseException {
    public ValidationException(final String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public ValidationException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }
}
