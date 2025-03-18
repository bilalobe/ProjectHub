package com.projecthub.auth.common.validator;

import org.passay.*;
import org.passay.dictionary.ArrayWordList;
import org.passay.dictionary.WordList;
import org.passay.dictionary.WordListDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * PasswordValidator is a component that validates passwords based on a set of rules.
 * It uses the Passay library to enforce various password policies.
 */
@Component
public class PasswordValidator {
    private static final Logger logger = LoggerFactory.getLogger(PasswordValidator.class);
    private static final int MAX_SEQUENCE_LENGTH = 3;
    private static final int MIN_UNIQUE_CHARS = 8;

    private static final WordList FORBIDDEN_WORDS = new ArrayWordList(
        new String[]{"projecthub", "password", "admin", "qwerty", "letmein", "welcome", "abc123", "123456", "monkey", "login", "user", "admin1", "bilal"},
        true
    );
    private static final Pattern PATTERN = Pattern.compile("\\s+");

    private final org.passay.PasswordValidator passayValidator;

    public PasswordValidator() {
        passayValidator = new org.passay.PasswordValidator(Arrays.asList(
            new LengthRule(12, 128),
            new CharacterRule(EnglishCharacterData.Digit, 2),
            new CharacterRule(EnglishCharacterData.LowerCase, 2),
            new CharacterRule(EnglishCharacterData.UpperCase, 2),
            new CharacterRule(EnglishCharacterData.Special, 2),
            new WhitespaceRule(),
            new DictionaryRule(new WordListDictionary(PasswordValidator.FORBIDDEN_WORDS)),
            new UsernameRule(true, true),
            new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
            new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false),
            new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
            new RepeatCharacterRegexRule(PasswordValidator.MAX_SEQUENCE_LENGTH), // Limit repeating chars
            new AllowedRegexRule("^[^\\s]*$"), // No whitespace
            new IllegalRegexRule("(.)\\1{" + (PasswordValidator.MAX_SEQUENCE_LENGTH - 1) + ",}"), // No char repeated MAX_SEQUENCE_LENGTH times or more
            new CharacterCharacteristicsRule(PasswordValidator.MIN_UNIQUE_CHARS, // At least MIN_UNIQUE_CHARS
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.Special, 1)
            ),
            new HistoryRule() // Prevents reuse of previous passwords
        ));
    }

    /**
     * Checks if the password is strong enough based on the defined rules.
     *
     * @param password The password to validate
     * @return true if the password meets all criteria, false otherwise
     */
    public boolean isStrongEnough(final String password) {
        if (null == password) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        final RuleResult result = this.passayValidator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        logger.debug("Password is not strong enough. See errors: {}", this.passayValidator.getMessages(result));
        return false;
    }

    /**
     * Validates the password and returns a list of validation error messages.
     *
     * @param password The password to validate
     * @return List of error messages, empty if valid
     */
    public List<String> validatePassword(final String password) {
        if (null == password) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        final RuleResult result = this.passayValidator.validate(new PasswordData(password));
        if (!result.isValid()) {
            return this.passayValidator.getMessages(result);
        }
        return Collections.emptyList();
    }

    /**
     * Validates the password and returns a list of formatted validation error messages.
     *
     * @param password The password to validate
     * @return List of formatted error messages, empty if valid
     */
    public List<String> getDetailedValidationErrors(final String password) {
        final RuleResult result = this.passayValidator.validate(new PasswordData(password));
        return result.isValid() ? Collections.emptyList() :
            this.passayValidator.getMessages(result).stream()
                .map(PasswordValidator::formatValidationMessage)
                .toList();
    }

    private static String formatValidationMessage(final String message) {
        return PATTERN.matcher(message.replaceAll("\\{\\d+}", "")).replaceAll(" ")
            .trim();
    }
}