package com.projecthub.core.exceptions;

import org.springframework.http.HttpStatus;

public class ValidationException extends BaseException {
    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }
}
