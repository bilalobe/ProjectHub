package com.projecthub.base.shared.exception;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends BaseException {
    /*
     * This exception is thrown when a resource already exists in the system.
     *
     * For example, when trying to create a new role with a name that already exists.
     *
     * This exception should be handled by the controller layer and return a 409 status code.
     */

    public ResourceAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
