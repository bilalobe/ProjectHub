package com.projecthub.base.shared.exception;

import org.springframework.http.HttpStatus;

public class SynchronizationException extends BaseException {
    public SynchronizationException(final String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public SynchronizationException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
