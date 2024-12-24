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
public class AppUserValidationServiceImpl implements AppUserValidationService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserValidationServiceImpl.class);

    private final Validator validator;
    private final AppUserJpaRepository userRepository;

    public AppUserValidationServiceImpl(Validator validator, AppUserJpaRepository userRepository) {
        this.validator = validator;
        this.userRepository = userRepository;
    }

    @Override
    public void validateRegistration(RegisterRequestDTO request) {
        logger.info("Validating registration request for username: {}", request.username());
        validateConstraints(request);
        validateUniqueUsername(request.username());
        validateUniqueEmail(request.email());
    }

    @Override
    public void validateUpdate(UpdateUserRequestDTO request, String currentUsername, String currentEmail) {
        logger.info("Validating update request for username: {}", request.username());
        validateConstraints(request);
        if (!request.username().equals(currentUsername)) {
            validateUniqueUsername(request.username());
        }
        if (!request.email().equals(currentEmail)) {
            validateUniqueEmail(request.email());
        }
    }

    @Override
    public void validateUniqueUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            logger.warn("Username already exists: {}", username);
            throw new ValidationException("Username already exists: " + username);
        }
    }

    @Override
    public void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            logger.warn("Email already exists: {}", email);
            throw new ValidationException("Email already exists: " + email);
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
}