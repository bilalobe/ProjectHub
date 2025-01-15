package com.projecthub.base.user.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(final String message) {
        super(message);
    }

    public UsernameAlreadyExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
