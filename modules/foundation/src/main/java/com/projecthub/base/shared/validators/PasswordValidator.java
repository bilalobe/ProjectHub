package com.projecthub.base.shared.validators;

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

/**
 * PasswordValidator is a component that validates passwords based on a set of rules.
 * It uses the Passay library to enforce various password policies such as length, character types,
 * forbidden words, sequences, and more.
 * <p>
 * The following rules are enforced:
 * - Length between 12 and 128 characters.
 * - At least 2 digits, 2 lowercase letters, 2 uppercase letters, and 2 special characters.
 * - No whitespace allowed.
 * - Forbidden words such as "projecthub", "password", "admin", etc.
 * - No sequences of 5 or more alphabetical, numerical, or QWERTY characters.
 * - No character repeated more than 3 times consecutively.
 * - At least 8 unique characters.
 * - Prevents reuse of previous passwords.
 * <p>
 * Methods:
 * - {@link #isStrongEnough(String)}: Checks if the password is strong enough based on the rules.
 * - {@link #validatePassword(String)}: Validates the password and returns a list of validation error messages.
 * - {@link #getDetailedValidationErrors(String)}: Validates the password and returns a list of formatted validation error messages.
 * <p>
 * Dependencies:
 * - Passay library for password validation rules.
 * - SLF4J for logging.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * PasswordValidator validator = new PasswordValidator();
 * boolean isValid = validator.isStrongEnough("ExamplePassword123!");
 * List<String> errors = validator.validatePassword("ExamplePassword123!");
 * }
 * </pre>
 * <p>
 * Note: This class is annotated with @Component, making it a Spring-managed bean.
 */
@Component
public
class PasswordValidator {
    private static final Logger logger = LoggerFactory.getLogger(PasswordValidator.class);
    private static final int MAX_SEQUENCE_LENGTH = 3;
    private static final int MIN_UNIQUE_CHARS = 8;

    private static final WordList FORBIDDEN_WORDS = new ArrayWordList(
        new String[]{"projecthub", "password", "admin", "qwerty", "letmein", "welcome", "abc123", "123456", "monkey", "login", "user", "admin1", "bilal"},
        true
    );

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
            new AllowedRegexRule("^[^\\s]*$"), // No whitespace (Keep this rule)
            new IllegalRegexRule("(.)\\1{" + (PasswordValidator.MAX_SEQUENCE_LENGTH - 1) + ",}"), // No char repeated MAX_SEQUENCE_LENGTH times or more (More Dynamic)
            new CharacterCharacteristicsRule(PasswordValidator.MIN_UNIQUE_CHARS, // At least MIN_UNIQUE_CHARS
                new CharacterRule(EnglishCharacterData.Digit, 1), // At least 1 digit
                new CharacterRule(EnglishCharacterData.LowerCase, 1), // At least 1 lowercase
                new CharacterRule(EnglishCharacterData.UpperCase, 1),// At least 1 uppercase
                new CharacterRule(EnglishCharacterData.Special, 1)// At least 1 special char
            ),
            new HistoryRule() // Prevents reuse of previous passwords
        ));
    }

    public boolean isStrongEnough(final String password) {
        if (null == password) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        final RuleResult result = this.passayValidator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        PasswordValidator.logger.debug("Password is not strong enough. See errors: {}", this.passayValidator.getMessages(result));
        return false;
    }

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

    public List<String> getDetailedValidationErrors(final String password) {
        final RuleResult result = this.passayValidator.validate(new PasswordData(password));
        return result.isValid() ? Collections.emptyList() :
            this.passayValidator.getMessages(result).stream()
                .map(this::formatValidationMessage)
                .toList();
    }

    private String formatValidationMessage(final String message) {
        return message.replaceAll("\\{\\d+}", "")
            .replaceAll("\\s+", " ")
            .trim();
    }
}
