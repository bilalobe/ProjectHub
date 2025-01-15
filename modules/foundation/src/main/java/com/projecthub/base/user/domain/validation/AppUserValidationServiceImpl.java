package com.projecthub.base.user.domain.validation;

import com.projecthub.base.auth.api.dto.RegisterRequestDTO;
import com.projecthub.base.repository.jpa.AppUserJpaRepository;
import com.projecthub.base.shared.exception.ValidationException;
import com.projecthub.base.user.api.dto.UpdateUserRequestDTO;
import com.projecthub.base.user.api.validation.AppUserValidationService;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AppUserValidationServiceImpl implements AppUserValidationService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserValidationServiceImpl.class);

    private final Validator validator;
    private final AppUserJpaRepository userRepository;

    public AppUserValidationServiceImpl(final Validator validator, final AppUserJpaRepository userRepository) {
        this.validator = validator;
        this.userRepository = userRepository;
    }

    @Override
    public void validateRegistration(final RegisterRequestDTO request) {
        AppUserValidationServiceImpl.logger.info("Validating registration request for username: {}", request.username());
        this.validateConstraints(request);
        this.validateUniqueUsername(request.username());
        this.validateUniqueEmail(request.email());
    }

    @Override
    public void validateUpdate(final UpdateUserRequestDTO request, final String currentUsername, final String currentEmail) {
        AppUserValidationServiceImpl.logger.info("Validating update request for username: {}", request.username());
        this.validateConstraints(request);
        if (!request.username().equals(currentUsername)) {
            this.validateUniqueUsername(request.username());
        }
        if (!request.email().equals(currentEmail)) {
            this.validateUniqueEmail(request.email());
        }
    }

    @Override
    public void validateUniqueUsername(final String username) {
        if (this.userRepository.existsByUsername(username)) {
            AppUserValidationServiceImpl.logger.warn("Username already exists: {}", username);
            throw new ValidationException("Username already exists: " + username);
        }
    }

    @Override
    public void validateUniqueEmail(final String email) {
        if (this.userRepository.existsByEmail(email)) {
            AppUserValidationServiceImpl.logger.warn("Email already exists: {}", email);
            throw new ValidationException("Email already exists: " + email);
        }
    }

    private void validateConstraints(final Object request) {
        final var violations = this.validator.validate(request);
        if (!violations.isEmpty()) {
            final String errors = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("");
            AppUserValidationServiceImpl.logger.error("Validation failed: {}", errors);
            throw new ValidationException("Validation failed: " + errors);
        }
    }
}
