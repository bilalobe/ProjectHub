package com.projecthub.core.exceptions;

import org.springframework.http.HttpStatus;

public class AccountDisabledException extends BaseException {
    public AccountDisabledException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public AccountDisabledException(String message, Throwable cause) {
        super(message, cause, HttpStatus.FORBIDDEN);
    }
}