package com.projecthub.core.exceptions;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends BaseException {
    public TokenExpiredException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause, HttpStatus.UNAUTHORIZED);
    }
}
