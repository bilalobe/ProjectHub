package com.projecthub.base.shared.exception;

import org.springframework.http.HttpStatus;

public class PasswordValidationException extends BaseException {
    public PasswordValidationException(final String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public PasswordValidationException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }
}
