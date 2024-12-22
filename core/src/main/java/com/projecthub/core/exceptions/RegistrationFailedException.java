package com.projecthub.core.exceptions;

import org.springframework.http.HttpStatus;

public class RegistrationFailedException extends BaseException {
    public RegistrationFailedException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public RegistrationFailedException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
