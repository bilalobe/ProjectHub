package com.projecthub.base.shared.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends BaseException {
    public InternalServerException(final String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalServerException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
