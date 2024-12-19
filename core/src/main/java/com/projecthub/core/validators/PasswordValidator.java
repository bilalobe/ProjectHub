package com.projecthub.core.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Validator class for password-related operations.
 */
@Component
public class PasswordValidator {

    private static final Logger logger = LoggerFactory.getLogger(PasswordValidator.class);
    // Define a regex pattern for password strength validation
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );
    private final PasswordEncoder passwordEncoder;

    public PasswordValidator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Encodes a raw password using the configured password encoder.
     *
     * @param rawPassword the raw password to encode
     * @return the encoded password
     * @throws IllegalArgumentException if rawPassword is null or empty
     */
    public String encodePassword(String rawPassword) {
        logger.info("Encoding password");
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Raw password cannot be null or empty");
        }
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Validates a raw password against the encoded password.
     *
     * @param rawPassword     the raw password to validate
     * @param encodedPassword the encoded password to compare against
     * @return true if the passwords match, false otherwise
     * @throws IllegalArgumentException if rawPassword or encodedPassword is null or empty
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        logger.info("Validating password");
        if (rawPassword == null || rawPassword.isEmpty() || encodedPassword == null || encodedPassword.isEmpty()) {
            throw new IllegalArgumentException("Passwords cannot be null or empty");
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Checks if a raw password meets the required strength criteria.
     *
     * @param rawPassword the raw password to check
     * @return true if the password meets the criteria, false otherwise
     * @throws IllegalArgumentException if rawPassword is null or empty
     */
    public boolean isPasswordStrong(String rawPassword) {
        logger.info("Checking password strength");
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Raw password cannot be null or empty");
        }
        return PASSWORD_PATTERN.matcher(rawPassword).matches();
    }

    /**
     * Validates if a password meets basic requirements.
     *
     * @param password the password to validate
     * @return true if password meets basic requirements, false otherwise
     * @throws IllegalArgumentException if password is null
     */
    public boolean isValid(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        logger.debug("Validating password requirements");

        if (password.length() < 8) {
            logger.debug("Password failed minimum length requirement");
            return false;
        }

        if (password.contains(" ")) {
            logger.debug("Password contains whitespace");
            return false;
        }

        boolean meetsRequirements = PASSWORD_PATTERN.matcher(password).matches();
        if (!meetsRequirements) {
            logger.debug("Password failed pattern requirements");
        }

        return meetsRequirements;
    }
}