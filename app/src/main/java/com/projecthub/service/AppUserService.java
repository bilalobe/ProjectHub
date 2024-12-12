package com.projecthub.service;

import com.projecthub.dto.AppUserDTO;
import com.projecthub.dto.RegisterRequestDTO;
import com.projecthub.dto.UpdateUserRequestDTO;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.AppUserMapper;
import com.projecthub.model.AppUser;
import com.projecthub.repository.jpa.AppUserJpaRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Service class for managing application users.
 * Provides functionalities to create, update, delete, and retrieve users,
 * as well as managing user passwords.
 */
@Service
public class AppUserService {

    private static final Logger logger = LoggerFactory.getLogger(AppUserService.class);

    private final AppUserJpaRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    // Define a regex pattern for password strength validation
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );

    /**
     * Constructs an AppUserService with the required dependencies.
     *
     * @param appUserRepository the repository for AppUser entities
     * @param appUserMapper     the mapper for converting between AppUser and AppUserDTO
     * @param passwordEncoder   the encoder for hashing passwords
     * @param validator         the validator for validating user details
     */
    public AppUserService(AppUserJpaRepository appUserRepository,
                          AppUserMapper appUserMapper,
                          PasswordEncoder passwordEncoder,
                          Validator validator) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    /**
     * Creates a new user with the provided registration details.
     *
     * @param registerRequest the registration request containing user details
     * @return the created AppUserDTO
     * @throws IllegalArgumentException if user already exists
     */
    @Transactional
    public AppUserDTO createUser(@Valid RegisterRequestDTO registerRequest) {
        logger.info("Creating a new user");

        validateRegisterRequest(registerRequest);

        // Check if username or email already exists
        if (appUserRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (appUserRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Encode the password
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Map RegisterRequestDTO to AppUser entity
        AppUser user = appUserMapper.toAppUser(registerRequest, encodedPassword);

        // Save the user
        AppUser savedUser = appUserRepository.save(user);

        logger.info("User created with ID: {}", savedUser.getId());

        // Map AppUser entity to AppUserDTO
        return appUserMapper.toAppUserDTO(savedUser);
    }

    /**
     * Updates an existing user with the provided update details.
     *
     * @param id                the UUID of the user to update
     * @param updateUserRequest the update request containing new user details
     * @return the updated AppUserDTO
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Transactional
    public AppUserDTO updateUser(UUID id, @Valid UpdateUserRequestDTO updateUserRequest) {
        logger.info("Updating user with ID: {}", id);

        validateUpdateRequest(updateUserRequest);

        // Find existing user
        AppUser existingUser = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Check for username or email conflicts with other users
        if (!existingUser.getUsername().equals(updateUserRequest.getUsername()) &&
                appUserRepository.existsByUsername(updateUserRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (!existingUser.getEmail().equals(updateUserRequest.getEmail()) &&
                appUserRepository.existsByEmail(updateUserRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Update fields
        existingUser.setFirstName(updateUserRequest.getFirstName());
        existingUser.setLastName(updateUserRequest.getLastName());
        existingUser.setEmail(updateUserRequest.getEmail());
        existingUser.setUsername(updateUserRequest.getUsername());

        // Update password if provided
        if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(updateUserRequest.getPassword());
            existingUser.setPassword(encodedPassword);
        }

        // Save updated user
        AppUser updatedUser = appUserRepository.save(existingUser);

        logger.info("User updated with ID: {}", updatedUser.getId());

        // Map AppUser entity to AppUserDTO
        return appUserMapper.toAppUserDTO(updatedUser);
    }

    /**
     * Deletes a user by their UUID.
     *
     * @param id the UUID of the user to delete
     * @throws ResourceNotFoundException if the user with the specified ID does not exist
     */
    @Transactional
    public void deleteUser(UUID id) {
        logger.info("Deleting user with ID: {}", id);
        if (!appUserRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        appUserRepository.deleteById(id);
        logger.info("User deleted with ID: {}", id);
    }

    /**
     * Resets the password for a user with the given UUID.
     *
     * @param id         the UUID of the user whose password is to be reset
     * @param rawPassword the new raw password
     * @throws ResourceNotFoundException if the user with the specified ID does not exist
     * @throws IllegalArgumentException  if rawPassword is invalid
     */
    @Transactional
    public void resetPassword(UUID id, String rawPassword) {
        logger.info("Resetting password for user with ID: {}", id);
        validatePasswordStrength(rawPassword);

        AppUser user = findUserById(id);
        user.setPassword(encodePassword(rawPassword));
        appUserRepository.save(user);

        logger.info("Password reset for user with ID: {}", user.getId());
    }

    /**
     * Updates the password for a user with the given UUID.
     *
     * @param id          the UUID of the user whose password is to be updated
     * @param newPassword the new raw password
     * @throws ResourceNotFoundException if the user with the specified ID does not exist
     * @throws IllegalArgumentException  if newPassword is invalid
     */
    @Transactional
    public void updateUserPassword(UUID id, String newPassword) {
        logger.info("Updating password for user with ID: {}", id);
        validatePasswordStrength(newPassword);

        AppUser user = findUserById(id);
        user.setPassword(encodePassword(newPassword));
        appUserRepository.save(user);

        logger.info("Password updated for user with ID: {}", user.getId());
    }

    /**
     * Retrieves a user by their UUID.
     *
     * @param id the UUID of the user to retrieve
     * @return the AppUserDTO of the retrieved user
     * @throws ResourceNotFoundException if the user with the specified ID does not exist
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
     * Encodes a raw password using the configured PasswordEncoder.
     *
     * @param rawPassword the raw password to encode
     * @return the encoded password
     * @throws IllegalArgumentException if rawPassword is null or empty
     */
    private String encodePassword(String rawPassword) {
        logger.info("Encoding password");
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Raw password cannot be null or empty");
        }
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Validates the strength of a raw password against the defined pattern.
     *
     * @param rawPassword the raw password to validate
     * @throws IllegalArgumentException if rawPassword does not meet strength requirements
     */
    private void validatePasswordStrength(String rawPassword) {
        logger.info("Validating password strength");
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (!PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw new IllegalArgumentException("Password does not meet strength requirements");
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
            throw new ConstraintViolationException("Registration validation failed", violations);
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
     * @throws ResourceNotFoundException if the user with the specified ID does not exist
     */
    private AppUser findUserById(UUID id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}