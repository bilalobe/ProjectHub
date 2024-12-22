package com.projecthub.core.exceptions;

import org.springframework.http.HttpStatus;

public class SynchronizationException extends BaseException {
    public SynchronizationException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public SynchronizationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}