package com.projecthub.base.auth.service;

import com.projecthub.base.auth.domain.exception.InvalidCredentialsException;
import com.projecthub.base.repository.jpa.AppUserJpaRepository;
import com.projecthub.base.shared.exception.PasswordValidationException;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
     * @param userRepository    the repository used to access user data
     */
    public PasswordService(final PasswordValidationService validationService,
                           final AppUserJpaRepository userRepository) {
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
     * @param userId          the UUID of the user whose password is to be updated
     * @param currentPassword the user's current password
     * @param newPassword     the new password to set for the user
     * @throws PasswordValidationException if the new password does not meet validation criteria
     * @throws ResourceNotFoundException   if no user is found with the provided user ID
     * @throws InvalidCredentialsException if the current password does not match the user's existing password
     */
    public void securelyUpdatePassword(final UUID userId, final String currentPassword, final String newPassword) {
        final AppUser user = this.userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        this.validationService.validateCredentials(currentPassword, user.getPassword());
        this.validationService.validateNewPassword(newPassword, user.getPassword());

        user.setPassword(this.validationService.encodePassword(newPassword));
        this.userRepository.save(user);
    }
}
