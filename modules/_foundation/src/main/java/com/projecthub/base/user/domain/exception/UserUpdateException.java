package com.projecthub.base.user.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserUpdateException extends RuntimeException {
    public UserUpdateException(final String message) {
        super(message);
    }

    public UserUpdateException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
