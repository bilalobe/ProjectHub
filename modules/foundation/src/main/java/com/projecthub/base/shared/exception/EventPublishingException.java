package com.projecthub.base.shared.exception;

public class EventPublishingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EventPublishingException(final String message) {
        super(message);
    }

    public EventPublishingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EventPublishingException(final Throwable cause) {
        super(cause);
    }

    public EventPublishingException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
