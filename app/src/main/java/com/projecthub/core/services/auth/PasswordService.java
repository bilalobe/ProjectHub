package com.projecthub.core.services.auth;

import com.projecthub.core.exceptions.PasswordValidationException;
import com.projecthub.core.validators.PasswordValidator;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final PasswordValidator passwordValidator;

    public PasswordService(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }

    public String encodePassword(String rawPassword) {
        validatePasswordStrength(rawPassword);
        return passwordValidator.encodePassword(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordValidator.matches(rawPassword, encodedPassword);
    }

    public void validatePasswordStrength(String rawPassword) {
        if (!passwordValidator.isPasswordStrong(rawPassword)) {
            throw new PasswordValidationException(
                    "Password must be at least 8 characters long, contain uppercase and lowercase letters, " +
                            "numbers, and special characters"
            );
        }
    }
}
