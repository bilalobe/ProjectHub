package com.projecthub.core.exceptions;

import org.springframework.http.HttpStatus;

public class AccountLockedException extends BaseException {
    public AccountLockedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public AccountLockedException(String message, Throwable cause) {
        super(message, cause, HttpStatus.FORBIDDEN);
    }
}