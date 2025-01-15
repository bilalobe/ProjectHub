package com.projecthub.base.shared.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {
    public ConflictException(final String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ConflictException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }
}
