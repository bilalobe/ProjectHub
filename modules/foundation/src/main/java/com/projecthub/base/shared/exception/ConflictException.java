package com.projecthub.base.shared.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }
}
