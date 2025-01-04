package com.projecthub.base.shared.exception;

public class EventPublishingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EventPublishingException(String message) {
        super(message);
    }

    public EventPublishingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventPublishingException(Throwable cause) {
        super(cause);
    }

    public EventPublishingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
