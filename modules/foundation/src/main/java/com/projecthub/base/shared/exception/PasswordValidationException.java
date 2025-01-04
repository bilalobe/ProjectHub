package com.projecthub.base.shared.exception;

import org.springframework.http.HttpStatus;

public class PasswordValidationException extends BaseException {
    public PasswordValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public PasswordValidationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }
}
