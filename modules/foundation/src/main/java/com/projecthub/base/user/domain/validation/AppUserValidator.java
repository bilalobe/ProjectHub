package com.projecthub.base.user.domain.validation;

import com.projecthub.base.auth.api.dto.RegisterRequestDTO;
import com.projecthub.base.user.api.dto.UpdateUserRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AppUserValidator {

    private final Validator validator;

    public AppUserValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateRegisterRequest(RegisterRequestDTO request) {
        validate(request);
    }

    public void validateUpdateRequest(UpdateUserRequestDTO request) {
        validate(request);
    }

    private <T> void validate(T request) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ValidationException(formatViolations(violations));
        }
    }


    private String formatViolations(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream()
            .map(this::formatViolation)
            .collect(Collectors.joining(", "));
    }

    private String formatViolation(ConstraintViolation<?> violation) {
        return String.format("%s: %s", violation.getPropertyPath(), violation.getMessage());
    }
}
