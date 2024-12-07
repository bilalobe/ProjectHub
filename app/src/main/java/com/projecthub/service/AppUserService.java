package com.projecthub.service;

import com.projecthub.dto.AppUserDTO;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.AppUserMapper;
import com.projecthub.model.AppUser;
import com.projecthub.model.Team;
import com.projecthub.repository.AppUserRepository;
import com.projecthub.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    private final AppUserRepository appUserRepository;
    private final TeamRepository teamRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;

    // Define a regex pattern for password strength validation
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );

    /**
     * Constructs an AppUserService with the required dependencies.
     *
     * @param appUserRepository the repository for AppUser entities
     * @param teamRepository    the repository for Team entities
     * @param appUserMapper     the mapper for converting between AppUser and AppUserDTO
     * @param passwordEncoder   the encoder for hashing passwords
     */
    public AppUserService(AppUserRepository appUserRepository,
                          TeamRepository teamRepository,
                          AppUserMapper appUserMapper,
                          PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.teamRepository = teamRepository;
        this.appUserMapper = appUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new user with the provided details and raw password.
     *
     * @param userDTO     the data transfer object containing user details
     * @param rawPassword the raw password for the user
     * @return the created AppUserDTO
     * @throws IllegalArgumentException if userDTO or rawPassword is invalid
     */
    @Transactional
    public AppUserDTO createUser(AppUserDTO userDTO, String rawPassword) {
        logger.info("Creating a new user");
        validateUserDTO(userDTO);
        validatePasswordStrength(rawPassword);

        Team team = findTeamById(userDTO.getTeamId());
        String encodedPassword = encodePassword(rawPassword);
        AppUser user = appUserMapper.toAppUser(userDTO, team, encodedPassword);
        AppUser savedUser = appUserRepository.save(user);

        logger.info("User created with ID: {}", savedUser.getId());
        return appUserMapper.toAppUserDTO(savedUser);
    }

    /**
     * Updates an existing user with the provided details and raw password.
     *
     * @param id          the UUID of the user to update
     * @param userDTO     the data transfer object containing updated user details
     * @param rawPassword the new raw password for the user (optional)
     * @return the updated AppUserDTO
     * @throws ResourceNotFoundException if the user with the specified ID does not exist
     * @throws IllegalArgumentException  if userDTO is invalid
     */
    @Transactional
    public AppUserDTO updateUser(UUID id, AppUserDTO userDTO, String rawPassword) {
        logger.info("Updating user with ID: {}", id);
        validateUserDTO(userDTO);

        AppUser existingUser = findUserById(id);
        Team team = findTeamById(userDTO.getTeamId());

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setTeam(team);

        if (rawPassword != null && !rawPassword.isEmpty()) {
            validatePasswordStrength(rawPassword);
            existingUser.setPassword(encodePassword(rawPassword));
        }

        AppUser updatedUser = appUserRepository.save(existingUser);

        logger.info("User updated with ID: {}", updatedUser.getId());
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

    /**
     * Finds a team by its UUID.
     *
     * @param teamId the UUID of the team to find
     * @return the Team entity
     * @throws ResourceNotFoundException if the team with the specified ID does not exist
     */
    private Team findTeamById(UUID teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + teamId));
    }

    /**
     * Validates the provided AppUserDTO for required fields.
     *
     * @param userDTO the AppUserDTO to validate
     * @throws IllegalArgumentException if userDTO or required fields are invalid
     */
    private void validateUserDTO(AppUserDTO userDTO) {
        if (userDTO == null) {
            throw new IllegalArgumentException("UserDTO cannot be null");
        }
        if (userDTO.getUsername() == null || userDTO.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (userDTO.getTeamId() == null) {
            throw new IllegalArgumentException("Team ID cannot be null");
        }
    }
}