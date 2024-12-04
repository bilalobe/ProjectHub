package com.projecthub.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, Locale locale) {
        String message = messageSource.getMessage("error.resource.not.found", null, locale);
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, message);
        logger.error("ResourceNotFoundException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex, Locale locale) {
        String message = messageSource.getMessage("error.illegal.argument", null, locale);
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, message);
        logger.error("IllegalArgumentException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handleIOException(IOException ex, Locale locale) {
        String message = messageSource.getMessage("error.io.exception", null, locale);
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, message);
        logger.error("IOException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex, Locale locale) {
        String message = messageSource.getMessage("error.access.denied", null, locale);
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, message);
        logger.error("AccessDeniedException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex, Locale locale) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String message = messageSource.getMessage(error, locale);
            errors.put(error.getField(), message);
        });
        logger.error("MethodArgumentNotValidException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleBindException(BindException ex, Locale locale) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + messageSource.getMessage(fieldError, locale))
                .reduce((message1, message2) -> message1 + ", " + message2)
                .orElse("Binding failed");
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, detail);
        logger.error("BindException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiError> handleInvalidCredentialsException(InvalidCredentialsException ex, Locale locale) {
        String message = messageSource.getMessage("error.invalid.credentials", null, locale);
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, message);
        logger.error("InvalidCredentialsException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleUserAlreadyExistsException(UserAlreadyExistsException ex, Locale locale) {
        String message = messageSource.getMessage("error.user.already.exists", null, locale);
        ApiError apiError = new ApiError(HttpStatus.CONFLICT, message);
        logger.error("UserAlreadyExistsException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiError> handleUnauthorizedAccessException(UnauthorizedAccessException ex, Locale locale) {
        String message = messageSource.getMessage("error.unauthorized.access", null, locale);
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, message);
        logger.error("UnauthorizedAccessException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleInvalidInputException(InvalidInputException ex, Locale locale) {
        String message = messageSource.getMessage("error.invalid.input", null, locale);
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, message);
        logger.error("InvalidInputException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleConflictException(ConflictException ex, Locale locale) {
        String message = messageSource.getMessage("error.conflict", null, locale);
        ApiError apiError = new ApiError(HttpStatus.CONFLICT, message);
        logger.error("ConflictException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handleException(Exception ex, Locale locale) {
        String message = messageSource.getMessage("error.internal.server", null, locale);
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, message);
        logger.error("Exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}