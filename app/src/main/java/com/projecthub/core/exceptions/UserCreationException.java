package com.projecthub.core.exceptions;

import org.springframework.http.HttpStatus;

public class UserCreationException extends BaseException {
    public UserCreationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public UserCreationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }
}
