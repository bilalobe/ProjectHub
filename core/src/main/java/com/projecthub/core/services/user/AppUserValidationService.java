package com.projecthub.core.services.user;

import com.projecthub.core.dto.RegisterRequestDTO;
import com.projecthub.core.dto.UpdateUserRequestDTO;
import com.projecthub.core.exceptions.ValidationException;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AppUserValidationService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserValidationService.class);

    private final Validator validator;
    private final AppUserJpaRepository userRepository;

    public AppUserValidationService(Validator validator, AppUserJpaRepository userRepository) {
        this.validator = validator;
        this.userRepository = userRepository;
    }

    public void validateRegistration(RegisterRequestDTO request) {
        logger.info("Validating registration request for username: {}", request.getUsername());
        validateConstraints(request);
        checkDuplicateUsername(request.getUsername());
        checkDuplicateEmail(request.getEmail());
    }

    public void validateUpdate(UpdateUserRequestDTO request, String currentUsername, String currentEmail) {
        logger.info("Validating update request for username: {}", request.getUsername());
        validateConstraints(request);
        if (!request.getUsername().equals(currentUsername)) {
            checkDuplicateUsername(request.getUsername());
        }
        if (!request.getEmail().equals(currentEmail)) {
            checkDuplicateEmail(request.getEmail());
        }
    }

    private void validateConstraints(Object request) {
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("");
            logger.error("Validation failed: {}", errors);
            throw new ValidationException("Validation failed: " + errors);
        }
    }

    private void checkDuplicateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            logger.warn("Username already exists: {}", username);
            throw new ValidationException("Username already exists: " + username);
        }
    }

    private void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            logger.warn("Email already exists: {}", email);
            throw new ValidationException("Email already exists: " + email);
        }
    }
}