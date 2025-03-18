package com.projecthub.base.shared.infrastructure.event;

/**
 * Exception thrown when message publishing fails.
 */
public class MessagePublishException extends RuntimeException {
    public MessagePublishException(String message) {
        super(message);
    }

    public MessagePublishException(String message, Throwable cause) {
        super(message, cause);
    }
}