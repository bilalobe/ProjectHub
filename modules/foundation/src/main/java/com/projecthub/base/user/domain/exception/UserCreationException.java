package com.projecthub.base.user.domain.exception;

import com.projecthub.base.shared.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserCreationException extends BaseException {
    public UserCreationException(final String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public UserCreationException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }
}
