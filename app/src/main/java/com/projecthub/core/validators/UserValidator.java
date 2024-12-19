package com.projecthub.core.validators;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.projecthub.core.dto.RegisterRequestDTO;
import com.projecthub.core.dto.UpdateUserRequestDTO;

@Component
public class UserValidator {
    private final Validator validator;

    public UserValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateRegisterRequest(RegisterRequestDTO request) {
        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ValidationException(formatViolations(violations));
        }
    }

    public void validateUpdateRequest(UpdateUserRequestDTO request) {
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ValidationException(formatViolations(violations));
        }
    }

    private String formatViolations(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
    }
}