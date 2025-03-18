package com.projecthub.base.shared.api.advice;

import com.projecthub.base.auth.domain.exception.*;
import com.projecthub.base.shared.exception.*;
import com.projecthub.base.user.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;

    public GlobalExceptionHandler(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private static ResponseEntity<ApiError> createErrorResponse(final HttpStatus status, final String message, final Exception ex) {
        final ApiError apiError = ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(status)
            .message(message)
            .debugMessage(ex.getLocalizedMessage())
            .build();

        GlobalExceptionHandler.LOG.error("Error occurred: {}", message, ex);
        return new ResponseEntity<>(apiError, status);
    }

    // Base exception handler
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiError> handleBaseException(final BaseException ex, final Locale locale) {
        return GlobalExceptionHandler.createErrorResponse(ex.getStatus(), this.getLocalizedMessage(ex.getMessage(), locale), ex);
    }

    // Authentication and Authorization exceptions
    @ExceptionHandler({
        UnauthorizedAccessException.class,
        InvalidCredentialsException.class,
        AccessDeniedException.class,
        AccountDisabledException.class,
        TokenExpiredException.class
    })
    public ResponseEntity<ApiError> handleAuthException(final Exception ex, final Locale locale) {
        final HttpStatus status;
        if (ex instanceof AccessDeniedException || ex instanceof AccountDisabledException) {
            status = HttpStatus.FORBIDDEN;
        } else if (ex instanceof UnauthorizedAccessException ||
            ex instanceof InvalidCredentialsException ||
            ex instanceof TokenExpiredException) {
            status = HttpStatus.UNAUTHORIZED;
        } else {
            status = HttpStatus.UNAUTHORIZED;
        }

        return GlobalExceptionHandler.createErrorResponse(
            status,
            this.getLocalizedMessage("error.authentication", locale),
            ex
        );
    }

    @ExceptionHandler({
        AuthenticationException.class,
        AuthenticationFailedException.class,
        InvalidTokenException.class
    })
    public ResponseEntity<ApiError> handleAuthenticationFailure(final BaseException ex, final Locale locale) {
        return GlobalExceptionHandler.createErrorResponse(HttpStatus.UNAUTHORIZED,
            this.getLocalizedMessage("error.authentication.failed", locale), ex);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ApiError> handleEmailError(final EmailSendingException ex, final Locale locale) {
        return GlobalExceptionHandler.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
            this.getLocalizedMessage("error.email.sending", locale), ex);
    }

    @ExceptionHandler({
        InvalidInputException.class,
        PasswordValidationException.class
    })
    public ResponseEntity<ApiError> handleInputValidation(final BaseException ex, final Locale locale) {
        return GlobalExceptionHandler.createErrorResponse(HttpStatus.BAD_REQUEST,
            this.getLocalizedMessage("error.input.validation", locale), ex);
    }

    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<ApiError> handleRegistrationFailure(final RegistrationFailedException ex, final Locale locale) {
        return GlobalExceptionHandler.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
            this.getLocalizedMessage("error.registration", locale), ex);
    }

    // Resource conflict exceptions
    @ExceptionHandler({
        UserAlreadyExistsException.class,
        EmailAlreadyExistsException.class,
        UsernameAlreadyExistsException.class,
        ConflictException.class,
        UserCreationException.class,
        UserUpdateException.class
    })
    public ResponseEntity<ApiError> handleConflictException(final RuntimeException ex, final Locale locale) {
        return GlobalExceptionHandler.createErrorResponse(
            HttpStatus.CONFLICT,
            this.getLocalizedMessage("error.conflict", locale),
            ex
        );
    }

    // Validation exceptions
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        @NonNull final MethodArgumentNotValidException ex,
        @NonNull final HttpHeaders headers,
        @NonNull final HttpStatusCode status,
        @NonNull final WebRequest request
    ) {
        final ApiError error = this.handleValidationErrors(ex, LocaleContextHolder.getLocale(), "error.validation").getBody();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiError> handleBindException(final BindException ex, final Locale locale) {
        return this.handleValidationErrors(ex, locale, "error.binding");
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(final ValidationException ex, final Locale locale) {
        return GlobalExceptionHandler.createErrorResponse(
            HttpStatus.BAD_REQUEST,
            this.getLocalizedMessage("error.validation", locale),
            ex
        );
    }

    private ResponseEntity<ApiError> handleValidationErrors(final Exception ex, final Locale locale, final String messageKey) {
        final List<ApiSubError> subErrors = new ArrayList<>();
        final BindingResult bindingResult = switch (ex) {
            case final MethodArgumentNotValidException methodEx -> methodEx.getBindingResult();
            case final BindException bindEx -> bindEx.getBindingResult();
            default -> null;
        };

        if (null != bindingResult) {
            bindingResult.getFieldErrors().forEach(error ->
                subErrors.add(new ApiValidationError(
                    error.getField(),
                    error.getRejectedValue(),
                    this.messageSource.getMessage(error, locale)
                ))
            );
        }

        final ApiError apiError = ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST)
            .message(this.getLocalizedMessage(messageKey, locale))
            .subErrors(subErrors)
            .build();

        GlobalExceptionHandler.LOG.error("Validation error: {}", messageKey, ex);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    // Resource not found exceptions
    @ExceptionHandler({
        ResourceNotFoundException.class,
        UserNotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFoundException(final RuntimeException ex, final Locale locale) {
        return GlobalExceptionHandler.createErrorResponse(
            HttpStatus.NOT_FOUND,
            this.getLocalizedMessage("error.not.found", locale),
            ex
        );
    }

    // Internal server errors
    @ExceptionHandler({
        RepositoryException.class,
        IOException.class,
        InternalServerException.class,
        SynchronizationException.class
    })
    public ResponseEntity<ApiError> handleInternalError(final Exception ex, final Locale locale) {
        return GlobalExceptionHandler.createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            this.getLocalizedMessage("error.internal", locale),
            ex
        );
    }

    @ExceptionHandler(SynchronizationException.class)
    public ResponseEntity<ApiError> handleSynchronizationException(final SynchronizationException ex, final Locale locale) {
        return GlobalExceptionHandler.createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            this.getLocalizedMessage("error.synchronization", locale),
            ex
        );
    }

    // Profile update exception
    @ExceptionHandler(ProfileUpdateException.class)
    public ResponseEntity<ApiError> handleProfileUpdateException(final ProfileUpdateException ex) {
        GlobalExceptionHandler.LOG.error("Profile update error", ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Failed to update profile")
                .debugMessage(ex.getMessage())
                .build());
    }

    // Storage exception
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ApiError> handleStorageException(final StorageException ex) {
        GlobalExceptionHandler.LOG.error("File storage error", ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("File storage error")
                .debugMessage(ex.getMessage())
                .build());
    }

    // Catch-all handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(final Exception ex, final Locale locale) {
        return GlobalExceptionHandler.createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            this.getLocalizedMessage("error.unexpected", locale),
            ex
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllOtherExceptions(
        final Exception ex, final WebRequest request) {
        GlobalExceptionHandler.LOG.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("An unexpected error occurred")
                .debugMessage(ex.getLocalizedMessage())
                .build());
    }

    private String getLocalizedMessage(final String key, final Locale locale) {
        return this.messageSource.getMessage(key, null, key, locale);
    }
}
