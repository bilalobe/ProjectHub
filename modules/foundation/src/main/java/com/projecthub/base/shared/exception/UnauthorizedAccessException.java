package com.projecthub.base.shared.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends BaseException {
    public UnauthorizedAccessException(final String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedAccessException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.UNAUTHORIZED);
    }
}
