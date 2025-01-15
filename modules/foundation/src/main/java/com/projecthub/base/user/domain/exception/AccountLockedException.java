package com.projecthub.base.user.domain.exception;

import com.projecthub.base.shared.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AccountLockedException extends BaseException {
    public AccountLockedException(final String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public AccountLockedException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.FORBIDDEN);
    }
}
