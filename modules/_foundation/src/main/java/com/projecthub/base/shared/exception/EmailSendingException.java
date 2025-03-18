package com.projecthub.base.shared.exception;

import org.springframework.http.HttpStatus;

public class EmailSendingException extends BaseException {
    public EmailSendingException(final String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public EmailSendingException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
