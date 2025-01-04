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

    public PasswordValidationService(PasswordValidator validator, PasswordEncoder passwordEncoder) {
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
    }

    public List<String> validatePassword(String password) {
        return validator.validatePassword(password);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Validates a new password against security policies and current password
     */
    public void validateNewPassword(String newPassword, String currentEncodedPassword) {
        List<String> errors = validatePassword(newPassword);
        if (!errors.isEmpty()) {
            throw new PasswordValidationException(String.join(", ", errors));
        }

        if (matches(newPassword, currentEncodedPassword)) {
            throw new PasswordValidationException("New password must be different");
        }
    }

    /**
     * Validates credentials and throws appropriate exceptions
     */
    public void validateCredentials(String rawPassword, String encodedPassword) {
        if (!matches(rawPassword, encodedPassword)) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    public String encodePassword(String rawPassword) {
        return encode(rawPassword);
    }
}
