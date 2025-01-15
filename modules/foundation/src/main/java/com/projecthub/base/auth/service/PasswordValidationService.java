package com.projecthub.base.auth.service;

import com.projecthub.base.shared.validators.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for password validation and encoding.
 * This class provides methods to validate, encode, and match passwords.
 *
 * @author
 */
@Service
public class PasswordValidationService {
    private final PasswordValidator validator;
    private final PasswordEncoder passwordEncoder;

    public PasswordValidationService(final PasswordValidator validator, final PasswordEncoder passwordEncoder) {
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
    }

    public List<String> validatePassword(final String password) {
        return this.validator.validatePassword(password);
    }

    public boolean matches(final String rawPassword, final String encodedPassword) {
        return this.passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String encode(final String rawPassword) {
        return this.passwordEncoder.encode(rawPassword);
    }

    /**
     * Validates a new password against security policies and current password
     */
    public void validateNewPassword(final String newPassword, final String currentEncodedPassword) {
        final List<String> errors = this.validatePassword(newPassword);
        if (!errors.isEmpty()) {
            throw new PasswordValidationException(String.join(", ", errors));
        }

        if (this.matches(newPassword, currentEncodedPassword)) {
            throw new PasswordValidationException("New password must be different");
        }
    }

    /**
     * Validates credentials and throws appropriate exceptions
     */
    public void validateCredentials(final String rawPassword, final String encodedPassword) {
        if (!this.matches(rawPassword, encodedPassword)) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    public String encodePassword(final String rawPassword) {
        return this.encode(rawPassword);
    }
}
