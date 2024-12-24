package com.projecthub.core.services.auth;

import com.projecthub.core.entities.AppUser;
import com.projecthub.core.exceptions.InvalidCredentialsException;
import com.projecthub.core.exceptions.PasswordValidationException;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;

/**
 * Service responsible for handling password-related operations for users.
 * <p>
 * This service provides functionalities to securely update a user's password by
 * validating the new password against defined security policies, ensuring that the
 * new password is different from the current one, and encoding the password before
 * saving it to the repository.
 * </p>
 * <p>
 * It interacts with {@link PasswordValidationService} to perform password validation
 * and with {@link AppUserJpaRepository} to retrieve and persist user data.
 * </p>
 * 
 * <p><strong>Responsibilities:</strong></p>
 * <ul>
 *     <li>Validating new passwords against security policies.</li>
 *     <li>Ensuring the new password differs from the current password.</li>
 *     <li>Encoding passwords before storage.</li>
 *     <li>Handling exceptions related to password validation and user retrieval.</li>
 * </ul>
 * 
 * <p><strong>Usage Example:</strong></p>
 * <pre>{@code
 * UUID userId = UUID.fromString("user-uuid-string");
 * String currentPassword = "OldPassw0rd!";
 * String newPassword = "NewStr0ngP@ss!";
 * 
 * passwordService.securelyUpdatePassword(userId, currentPassword, newPassword);
 * }</pre>
 *
 * @see PasswordValidationService
 * @see AppUserJpaRepository
 */
@Service
public class PasswordService {
    
    private final PasswordValidationService validationService;
    private final AppUserJpaRepository userRepository;

    /**
     * Constructs a new {@code PasswordService} with the specified dependencies.
     *
     * @param validationService the service responsible for password validation and encoding
     * @param userRepository the repository used to access user data
     */
    public PasswordService(PasswordValidationService validationService, 
                           AppUserJpaRepository userRepository) {
        this.validationService = validationService;
        this.userRepository = userRepository;
    }

    /**
     * Securely updates the password for a user identified by the given user ID.
     * <p>
     * The method performs the following steps:
     * </p>
     * <ol>
     *     <li>Validates the new password against security policies.</li>
     *     <li>Retrieves the user from the repository using the provided user ID.</li>
     *     <li>Verifies that the current password matches the user's existing password.</li>
     *     <li>Ensures that the new password is different from the current password.</li>
     *     <li>Encodes the new password and updates the user's password in the repository.</li>
     * </ol>
     *
     * @param userId the UUID of the user whose password is to be updated
     * @param currentPassword the user's current password
     * @param newPassword the new password to set for the user
     *
     * @throws PasswordValidationException if the new password does not meet validation criteria
     * @throws ResourceNotFoundException if no user is found with the provided user ID
     * @throws InvalidCredentialsException if the current password does not match the user's existing password
     */
    public void securelyUpdatePassword(UUID userId, String currentPassword, String newPassword) {
        // Validate the new password
        List<String> errors = validationService.validatePassword(newPassword);
        if (!errors.isEmpty()) {
            throw new PasswordValidationException(String.join(", ", errors));
        }

        // Retrieve the user by ID
        AppUser user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify the current password
        if (!validationService.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // Ensure the new password is different from the current password
        if (validationService.matches(newPassword, user.getPassword())) {
            throw new PasswordValidationException("New password must be different");
        }

        // Encode and set the new password
        user.setPassword(validationService.encodePassword(newPassword));
        userRepository.save(user);
    }
}