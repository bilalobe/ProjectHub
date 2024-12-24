package com.projecthub.core.services.auth;

import com.projecthub.core.validators.PasswordValidator;
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
}