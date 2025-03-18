package com.projecthub.base.user.domain.validation;

import com.projecthub.base.auth.api.dto.RegisterRequestDTO;
import com.projecthub.base.user.api.dto.UpdateUserRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AppUserValidator {

    private final Validator validator;

    public AppUserValidator(final Validator validator) {
        this.validator = validator;
    }

    public void validateRegisterRequest(final RegisterRequestDTO request) {
        this.validate(request);
    }

    public void validateUpdateRequest(final UpdateUserRequestDTO request) {
        this.validate(request);
    }

    private <T> void validate(final T request) {
        final Set<ConstraintViolation<T>> violations = this.validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ValidationException(this.formatViolations(violations));
        }
    }


    private String formatViolations(final Collection<? extends ConstraintViolation<?>> violations) {
        return violations.stream()
            .map(AppUserValidator::formatViolation)
            .collect(Collectors.joining(", "));
    }

    private static String formatViolation(final ConstraintViolation<?> violation) {
        return String.format("%s: %s", violation.getPropertyPath(), violation.getMessage());
    }
}
