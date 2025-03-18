package com.projecthub.auth.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for all application exceptions.
 * Provides consistent error handling with HTTP status.
 */
@Getter
public abstract class BaseException extends RuntimeException {
    private final HttpStatus status;

    protected BaseException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    protected BaseException(final String message, final Throwable cause, final HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}