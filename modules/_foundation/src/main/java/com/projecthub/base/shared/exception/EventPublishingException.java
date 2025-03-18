package com.projecthub.base.shared.exception;

import org.springframework.http.HttpStatus;

public class EventPublishingException extends BaseException {
    private static final long serialVersionUID = 1L;

    public EventPublishingException(final String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public EventPublishingException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
