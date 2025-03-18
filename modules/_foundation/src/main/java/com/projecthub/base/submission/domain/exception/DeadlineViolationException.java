package com.projecthub.base.submission.domain.exception;

import com.projecthub.base.shared.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DeadlineViolationException extends BaseException {
    public DeadlineViolationException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public DeadlineViolationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }
}
