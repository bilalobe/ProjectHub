package com.projecthub.base.shared.exception;

import org.springframework.http.HttpStatus;

public class ProfileUpdateException extends BaseException {
    public ProfileUpdateException(final String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ProfileUpdateException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
