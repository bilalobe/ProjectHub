package com.projecthub.core.services;

import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.dto.RegisterRequestDTO;
import com.projecthub.core.dto.UpdateUserRequestDTO;
import com.projecthub.core.exceptions.AccountDisabledException;
import com.projecthub.core.exceptions.AccountLockedException;
import com.projecthub.core.exceptions.AuthenticationFailedException;
import com.projecthub.core.exceptions.EmailAlreadyExistsException;
import com.projecthub.core.exceptions.InvalidCredentialsException;
import com.projecthub.core.exceptions.PasswordValidationException;
import com.projecthub.core.exceptions.UserAlreadyExistsException;
import com.projecthub.core.exceptions.UserCreationException;
import com.projecthub.core.exceptions.UserNotFoundException;
import com.projecthub.core.exceptions.UserUpdateException;
import com.projecthub.core.mappers.AppUserMapper;
import com.projecthub.core.models.AppUser;
import com.projecthub.core.models.GithubUserInfo;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
import com.projecthub.core.services.auth.GithubOAuthService;
import com.projecthub.core.validators.PasswordValidator;

import jakarta.security.auth.message.AuthException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @deprecated This service has been split into smaller, more focused services:
 * - {@link com.projecthub.core.services.user.UserRegistrationService}
 * - {@link com.projecthub.core.services.auth.AuthenticationService}
 * - {@link com.projecthub.core.services.auth.PasswordService}
 * - {@link com.projecthub.core.services.auth.AccountLockingService}
 * - {@link com.projecthub.core.services.account.AccountManagementService}
 * - {@link com.projecthub.core.services.user.AppUserProfileService}
 * <p>
 * This service will be removed in the next major version.
 */
@Deprecated(forRemoval = true)
@Service
public class AppUserService {

    private static final Logger logger = LoggerFactory.getLogger(AppUserService.class);
    private static final String PASSWORD_STRENGTH_ERROR = "Password does not meet strength requirements";
    private static final String USER_NOT_FOUND_ERROR = "User not found with ID: ";
    private final AppUserJpaRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordValidator passwordValidator;
    private final Validator validator;
    private final GithubOAuthService githubOAuthService;

    /**
     * Constructs an AppUserService with the required dependencies.
     *
     * @param appUserRepository  the repository for AppUser entities
     * @param appUserMapper      the mapper for converting between AppUser and AppUserDTO
     * @param passwordValidator  the validator for validating and encoding passwords
     * @param validator          the validator for validating user details
     * @param githubOAuthService the service for handling GitHub OAuth
     */
    public AppUserService(AppUserJpaRepository appUserRepository,
                          AppUserMapper appUserMapper,
                          PasswordValidator passwordValidator,
                          Validator validator,
                          GithubOAuthService githubOAuthService) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
        this.passwordValidator = passwordValidator;
        this.validator = validator;
        this.githubOAuthService = githubOAuthService;
    }

    /**
     * Registers a new user with the provided registration details.
     *
     * @param registerRequest the registration request containing user details
     * @return the created AppUserDTO
     * @throws IllegalArgumentException if user already exists
     */
    @Transactional
    public AppUserDTO registerUser(@Valid RegisterRequestDTO registerRequest) {
        logger.info("Registering a new user");

        validateRegisterRequest(registerRequest);

        // Check if username or email already exists
        if (appUserRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (appUserRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Validate password strength
        if (!passwordValidator.isPasswordStrong(registerRequest.getPassword())) {
            throw new IllegalArgumentException(PASSWORD_STRENGTH_ERROR);
        }

        // Encode the password
        String encodedPassword = passwordValidator.encodePassword(registerRequest.getPassword());

        // Map RegisterRequestDTO to AppUser entity
        AppUser user = appUserMapper.toAppUser(registerRequest, encodedPassword);

        // Save the user
        AppUser savedUser = appUserRepository.save(user);

        logger.info("User registered with ID: {}", savedUser.getId());

        // Map AppUser entity to AppUserDTO
        return appUserMapper.toAppUserDTO(savedUser);
    }

    /**
     * Creates a new user with the provided registration details.
     *
     * @param registerRequest the registration request containing user details
     * @return the created AppUserDTO
     * @throws UserCreationException if user creation fails
     */
    @Transactional
    public AppUserDTO createUser(@Valid RegisterRequestDTO registerRequest) {
        logger.info("Creating a new user with username: {}", registerRequest.getUsername());
        validateNewUserRequest(registerRequest);

        String encodedPassword = encodeAndValidatePassword(registerRequest.getPassword());
        AppUser user = createUserEntity(registerRequest, encodedPassword);

        try {
            AppUser savedUser = appUserRepository.save(user);
            logger.info("Successfully created user with ID: {}", savedUser.getId());
            return appUserMapper.toAppUserDTO(savedUser);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation while creating user: {}", registerRequest.getUsername(), e);
            throw new UserCreationException("Failed to create user due to data conflict", e);
        }
    }

    private void validateNewUserRequest(RegisterRequestDTO request) {
        validateRegisterRequest(request);
        checkForExistingUser(request.getUsername(), request.getEmail());
    }

    private void checkForExistingUser(String username, String email) {
        if (appUserRepository.existsByUsername(username)) {
            logger.warn("Username already exists: {}", username);
            throw new UserAlreadyExistsException("Username already exists: " + username);
        }
        if (appUserRepository.existsByEmail(email)) {
            logger.warn("Email already exists: {}", email);
            throw new EmailAlreadyExistsException("Email already exists: " + email);
        }
    }

    private String encodeAndValidatePassword(String rawPassword) {
        validatePasswordStrength(rawPassword);
        return passwordValidator.encodePassword(rawPassword);
    }

    private AppUser createUserEntity(RegisterRequestDTO request, String encodedPassword) {
        AppUser user = appUserMapper.toAppUser(request, encodedPassword);
        user.setCreatedAt(LocalDateTime.now());
        user.setEnabled(true);
        return user;
    }

    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        logger.info("Authenticating user: {}", username);

        return appUserRepository.findByUsername(username)
                .map(user -> validateUserAuthentication(user, password))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));
    }

    private boolean validateUserAuthentication(AppUser user, String password) {
        if (!user.isEnabled()) {
            throw new AccountDisabledException("Account is disabled");
        }
        if (!user.isAccountNonLocked()) {
            throw new AccountLockedException("Account is locked");
        }
        if (!passwordValidator.matches(password, user.getPassword())) {
            handleFailedLogin(user);
            throw new InvalidCredentialsException("Invalid username or password");
        }
        return true;
    }

    private void handleFailedLogin(AppUser user) {
        user.setFailedAttempts(user.getFailedAttempts() + 1);
        if (user.getFailedAttempts() >= 5) {
            user.setAccountNonLocked(false);
            logUserEvent(user.getId(), "ACCOUNT_LOCKED", "Account locked due to multiple failed attempts");
        }
        appUserRepository.save(user);
    }

    @Transactional
    public void resetFailedAttempts(UUID userId) {
        AppUser user = findUserById(userId);
        user.setFailedAttempts(0);
        appUserRepository.save(user);
    }

    @Transactional
    public void disableAccount(UUID userId, String reason) {
        AppUser user = findUserById(userId);
        user.setEnabled(false);
        appUserRepository.save(user);
        logUserEvent(userId, "ACCOUNT_DISABLED", reason);
    }

    @Transactional
    public void enableAccount(UUID userId) {
        AppUser user = findUserById(userId);
        user.setEnabled(true);
        user.setFailedAttempts(0);
        appUserRepository.save(user);
        logUserEvent(userId, "ACCOUNT_ENABLED", "Account manually enabled");
    }

    /**
     * Updates an existing user with the provided update details.
     *
     * @param id                the UUID of the user to update
     * @param updateUserRequest the update request containing new user details
     * @return the updated AppUserDTO
     * @throws UserNotFoundException if the user does not exist
     */
    @Transactional
    public AppUserDTO updateUser(UUID id, @Valid UpdateUserRequestDTO updateUserRequest) {
        logger.info("Updating user with ID: {}", id);

        validateUpdateRequest(updateUserRequest);
        AppUser existingUser = findUserById(id);
        validateUpdateConflicts(existingUser, updateUserRequest);

        updateUserFields(existingUser, updateUserRequest);

        try {
            AppUser updatedUser = appUserRepository.save(existingUser);
            logger.info("Successfully updated user with ID: {}", updatedUser.getId());
            return appUserMapper.toAppUserDTO(updatedUser);
        } catch (DataIntegrityViolationException e) {
            logger.error("Failed to update user with ID: {}", id, e);
            throw new UserUpdateException("Failed to update user", e);
        }
    }

    private void validateUpdateConflicts(AppUser existingUser, UpdateUserRequestDTO request) {
        if (!existingUser.getUsername().equals(request.getUsername())) {
            checkUsernameAvailable(request.getUsername());
        }
        if (!existingUser.getEmail().equals(request.getEmail())) {
            checkEmailAvailable(request.getEmail());
        }
    }

    private void updateUserFields(AppUser user, UpdateUserRequestDTO request) {
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(encodeAndValidatePassword(request.getPassword()));
        }

        user.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Deletes a user by their UUID.
     *
     * @param id the UUID of the user to delete
     * @throws UserNotFoundException if the user with the specified ID does not exist
     */
    @Transactional
    public void deleteUser(UUID id) {
        logger.info("Deleting user with ID: {}", id);
        if (!appUserRepository.existsById(id)) {
            throw new UserNotFoundException(USER_NOT_FOUND_ERROR + id);
        }
        appUserRepository.deleteById(id);
        logger.info("User deleted with ID: {}", id);
    }

    /**
     * Updates the password for a user with the given UUID.
     *
     * @param id          the UUID of the user whose password is to be updated
     * @param newPassword the new raw password
     * @throws UserNotFoundException    if the user with the specified ID does not exist
     * @throws IllegalArgumentException if newPassword is invalid
     */
    @Transactional
    public void updateUserPassword(UUID id, String newPassword) {
        logger.info("Updating password for user with ID: {}", id);
        validatePasswordStrength(newPassword);

        AppUser user = findUserById(id);
        user.setPassword(passwordValidator.encodePassword(newPassword));
        appUserRepository.save(user);

        logger.info("Password updated for user with ID: {}", user.getId());
    }

    /**
     * Authenticates a user using OAuth with GitHub.
     */
    @Transactional
    public AppUserDTO authenticateWithGithub(String code) throws AuthException {
        logger.info("Authenticating user with GitHub");
        String accessToken = githubOAuthService.getGithubAccessToken(code);
        if (accessToken == null) {
            throw new AuthException("Failed to authenticate with GitHub");
        }

        GithubUserInfo userInfo = githubOAuthService.getGithubUserInfo(accessToken);
        if (userInfo == null) {
            throw new AuthenticationFailedException("Failed to get GitHub user info");
        }

        // Find or create user
        return appUserRepository.findByEmail(userInfo.getEmail())
                .map(appUserMapper::toAppUserDTO)
                .orElseGet(() -> createGithubUser(userInfo));
    }

    /**
     * Updates user's 2FA settings.
     */
    @Transactional
    public void updateTwoFactorAuth(UUID userId, boolean enable) {
        logger.info("Updating 2FA settings for user {}", userId);
        AppUser user = findUserById(userId);
        user.setTwoFactorEnabled(enable);
        appUserRepository.save(user);
    }

    /**
     * Retrieves a user by their UUID.
     *
     * @param id the UUID of the user to retrieve
     * @return the AppUserDTO of the retrieved user
     * @throws UserNotFoundException if the user with the specified ID does not exist
     */
    public AppUserDTO getUserById(UUID id) {
        logger.info("Retrieving user with ID: {}", id);
        AppUser user = findUserById(id);
        return appUserMapper.toAppUserDTO(user);
    }

    /**
     * Retrieves all users in the system.
     *
     * @return a list of AppUserDTOs representing all users
     */
    public List<AppUserDTO> getAllUsers() {
        logger.info("Retrieving all users");
        return appUserRepository.findAll().stream()
                .map(appUserMapper::toAppUserDTO)
                .toList();
    }

    /**
     * Validates the strength of a raw password using PasswordValidator.
     *
     * @param rawPassword the raw password to validate
     * @throws IllegalArgumentException if rawPassword does not meet strength requirements
     */
    private void validatePasswordStrength(String rawPassword) {
        try {
            if (!passwordValidator.isPasswordStrong(rawPassword)) {
                throw new PasswordValidationException("Password does not meet strength requirements: " +
                        "Must be at least 8 characters long, contain uppercase and lowercase letters, " +
                        "numbers, and special characters");
            }
        } catch (Exception e) {
            throw new PasswordValidationException("Password validation failed: " + e.getMessage());
        }
    }

    /**
     * Validates the RegisterRequestDTO.
     *
     * @param registerRequest the registration request to validate
     * @throws ConstraintViolationException if validation fails
     */
    private void validateRegisterRequest(RegisterRequestDTO registerRequest) {
        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(registerRequest);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            logger.warn("Registration validation failed: {}", errorMessage);
            throw new ValidationException("Registration validation failed: " + errorMessage);
        }
    }

    /**
     * Validates the UpdateUserRequestDTO.
     *
     * @param updateUserRequest the update request to validate
     * @throws ConstraintViolationException if validation fails
     */
    private void validateUpdateRequest(UpdateUserRequestDTO updateUserRequest) {
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Update validation failed", violations);
        }
    }

    /**
     * Finds a user by their UUID.
     *
     * @param id the UUID of the user to find
     * @return the AppUser entity
     * @throws UserNotFoundException if the user with the specified ID does not exist
     */
    private AppUser findUserById(UUID id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ERROR + id));
    }

    private AppUserDTO createGithubUser(GithubUserInfo userInfo) {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername(userInfo.getLogin());
        registerRequest.setEmail(userInfo.getEmail());
        registerRequest.setFirstName(userInfo.getName());
        registerRequest.setPassword(UUID.randomUUID().toString());

        AppUser user = appUserMapper.toAppUser(registerRequest,
                passwordValidator.encodePassword(registerRequest.getPassword()));
        user.setGithubId(userInfo.getLogin());
        user.setAvatarUrl(userInfo.getAvatarUrl());

        AppUser savedUser = appUserRepository.save(user);
        logger.info("Created new user from GitHub: {}", savedUser.getId());

        return appUserMapper.toAppUserDTO(savedUser);
    }

    @Transactional
    public void lockAccount(UUID userId) {
        logger.info("Locking account for user ID: {}", userId);
        AppUser user = findUserById(userId);
        user.setAccountNonLocked(false);
        appUserRepository.save(user);
    }

    @Transactional
    public void unlockAccount(UUID userId) {
        logger.info("Unlocking account for user ID: {}", userId);
        AppUser user = findUserById(userId);
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        appUserRepository.save(user);
    }

    private void logUserEvent(UUID userId, String event, String details) {
        logger.info("User {} - {}: {}", userId, event, details);
        // Additional audit logging implementation
    }
}