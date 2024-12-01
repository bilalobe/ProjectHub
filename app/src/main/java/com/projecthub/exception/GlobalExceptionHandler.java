package com.projecthub.exception;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Handles ResourceNotFoundException.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, ex.getMessage())
                                            .type(URI.create("https://example.com/not-found"))
                                            .title("Resource Not Found")
                                            .build(messageSource, Locale.getDefault());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles IllegalArgumentException.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
                                            .type(URI.create("https://example.com/bad-request"))
                                            .title("Invalid Argument")
                                            .build(messageSource, Locale.getDefault());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles IOException.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        ErrorResponse error = ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage())
                                            .type(URI.create("https://example.com/internal-server-error"))
                                            .title("Internal Server Error")
                                            .build(messageSource, Locale.getDefault());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles AccessDeniedException.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse error = ErrorResponse.builder(ex, HttpStatus.FORBIDDEN, ex.getMessage())
                                            .type(URI.create("https://example.com/forbidden"))
                                            .title("Access Denied")
                                            .build(messageSource, Locale.getDefault());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles MethodArgumentNotValidException.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                          .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                          .reduce((message1, message2) -> message1 + ", " + message2)
                          .orElse("Validation failed");
        ErrorResponse error = ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, detail)
                                            .type(URI.create("https://example.com/validation-error"))
                                            .title("Validation Error")
                                            .build(messageSource, Locale.getDefault());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles BindException.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                          .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                          .reduce((message1, message2) -> message1 + ", " + message2)
                          .orElse("Binding failed");
        ErrorResponse error = ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, detail)
                                            .type(URI.create("https://example.com/binding-error"))
                                            .title("Binding Error")
                                            .build(messageSource, Locale.getDefault());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other exceptions.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse error = ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred")
                                            .type(URI.create("https://example.com/internal-server-error"))
                                            .title("Internal Server Error")
                                            .build(messageSource, Locale.getDefault());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Other exception handlers...

}