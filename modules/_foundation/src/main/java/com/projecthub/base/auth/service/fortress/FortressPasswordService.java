package com.projecthub.base.auth.service.fortress;

import org.apache.directory.fortress.core.model.User;
import org.apache.directory.fortress.core.util.crypto.EncryptMgr;
import org.apache.directory.fortress.core.util.crypto.EncryptUtil;
import org.jetbrains.annotations.NonNls;
import org.passay.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for password generation and validation using Fortress encryption
 * capabilities and Passay for policy enforcement.
 */
@Service
public class FortressPasswordService {

    private static final Logger log = LoggerFactory.getLogger(FortressPasswordService.class);

    private final EncryptMgr encryptionManager;
    private final PasswordGenerator passwordGenerator;
    private final PasswordValidator passwordValidator;

    @Value("${projecthub.auth.passwords.min-length:12}")
    private int minLength;

    @Value("${projecthub.auth.passwords.max-length:64}")
    private int maxLength;

    @Value("${projecthub.auth.passwords.min-uppercase:1}")
    private int minUppercase;

    @Value("${projecthub.auth.passwords.min-lowercase:1}")
    private int minLowercase;

    @Value("${projecthub.auth.passwords.min-digits:1}")
    private int minDigits;

    @Value("${projecthub.auth.passwords.min-special:1}")
    private int minSpecial;

    public FortressPasswordService() {
        this.encryptionManager = EncryptUtil.getEncryptInstance();
        this.passwordGenerator = new PasswordGenerator();

        // Create password validation rules
        List<Rule> rules = new ArrayList<>();
        rules.add(new org.passay.LengthRule(minLength, maxLength));
        rules.add(new CharacterRule(EnglishCharacterData.UpperCase, minUppercase));
        rules.add(new CharacterRule(EnglishCharacterData.LowerCase, minLowercase));
        rules.add(new CharacterRule(EnglishCharacterData.Digit, minDigits));
        rules.add(new CharacterRule(EnglishCharacterData.Special, minSpecial));

        this.passwordValidator = new PasswordValidator(rules);
    }

    /**
     * Generates a cryptographically secure password that meets the password policy.
     *
     * @return a secure password
     */
    public String generateSecurePassword() {
        // Define character rules for password generation
        List<CharacterRule> rules = new ArrayList<>();
        rules.add(new CharacterRule(EnglishCharacterData.UpperCase, minUppercase));
        rules.add(new CharacterRule(EnglishCharacterData.LowerCase, minLowercase));
        rules.add(new CharacterRule(EnglishCharacterData.Digit, minDigits));
        rules.add(new CharacterRule(EnglishCharacterData.Special, minSpecial));

        // Generate a password that meets the rules
        String password = passwordGenerator.generatePassword(minLength, rules);
        log.debug("Generated secure password with length: {}", Integer.valueOf(password.length()));

        return password;
    }

    /**
     * Validates a password against the password policy.
     *
     * @param password the password to validate
     * @return list of validation errors, empty if password is valid
     */
    public List<String> validatePassword(String password) {
        RuleResult result = passwordValidator.validate(new PasswordData(password));

        if (result.isValid()) {
            return List.of();
        }

        return passwordValidator.getMessages(result);
    }

    /**
     * Encrypt a password using Fortress encryption.
     *
     * @param password the clear text password
     * @return the encrypted password
     */
    public String encryptPassword(String password) {
        return encryptionManager.encrypt(password);
    }

    /**
     * Check if a provided password matches a stored encrypted password.
     *
     * @param providedPassword the password to check
     * @param storedPassword the encrypted password to check against
     * @return true if the password matches
     */
    public boolean checkPassword(String providedPassword, String storedPassword) {
        return encryptionManager.checkPassword(providedPassword, storedPassword);
    }

    /**
     * Update a user's password in Fortress.
     *
     * @param user the Fortress user
     * @param newPassword the new password
     * @return the user with encrypted password
     */
    public User updateUserPassword(User user, String newPassword) {
        // Validate the password against policy
        List<String> validationErrors = validatePassword(newPassword);
        if (!validationErrors.isEmpty()) {
            @NonNls String errors = String.join(", ", validationErrors);
            log.error("Password validation failed: {}", errors);
            throw new IllegalArgumentException("Password does not meet requirements: " + errors);
        }

        // Set encrypted password on user
        String encrypted = encryptPassword(newPassword);
        user.setPassword(encrypted);

        return user;
    }
}
