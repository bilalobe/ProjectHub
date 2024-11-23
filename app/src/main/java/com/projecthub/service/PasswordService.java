package com.projecthub.service;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordService.class);

    private final PasswordEncoder passwordEncoder;

    // Define a regex pattern for password strength validation
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );

    public PasswordService(PasswordEncoder passwordEncoder) {
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
     * @param rawPassword the raw password to validate
     * @param encodedPassword the encoded password to compare against
     * @return true if the passwords match, false otherwise
     * @throws IllegalArgumentException if rawPassword or encodedPassword is null or empty
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
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
}