package com.projecthub.core.services.user;

import com.projecthub.core.dto.AppUserCredentialsDTO;
import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.dto.RegisterRequestDTO;
import com.projecthub.core.entities.AppUser;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.mappers.AppUserMapper;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
import com.projecthub.core.services.registration.AppUserRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing application users.
 */
@Service
public class AppUserManagementService {

    private static final Logger logger = LoggerFactory.getLogger(AppUserManagementService.class);
    private static final String USER_NOT_FOUND = "User not found with ID: ";

    private final AppUserJpaRepository appUserRepository;
    private final AppUserMapper userMapper;
    private final AppUserRegistrationService registrationService;
    private final PasswordEncoder passwordEncoder;

    public AppUserManagementService(
            AppUserJpaRepository appUserRepository,
            AppUserMapper userMapper,
            AppUserRegistrationService registrationService,
            PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.userMapper = userMapper;
        this.registrationService = registrationService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves all users.
     *
     * @return a list of user DTOs
     */
    public List<AppUserDTO> getAllUsers() {
        logger.info("Retrieving all users");
        return appUserRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the ID of the user to retrieve
     * @return the user DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    public AppUserDTO getUserById(UUID id) {
        logger.info("Retrieving user with ID: {}", id);
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + id));
        return userMapper.toDto(user);
    }

    /**
     * Saves a new user.
     *
     * @param userDTO the user data transfer object
     * @return the saved user DTO
     */
    @Transactional
    public AppUserDTO saveUser(AppUserDTO userDTO) {
        logger.info("Saving new user");

        // Encode the password using Spring Security's PasswordEncoder
        String encodedPassword = passwordEncoder.encode(userDTO.password());

        var credentials = new AppUserCredentialsDTO(userDTO.username(), encodedPassword);
        var registerRequest = new RegisterRequestDTO(
            credentials,
            userDTO.email(),
            userDTO.username()
        );

        AppUser user = userMapper.toEntity(registerRequest, userDTO.username());
        AppUser savedUser = appUserRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    /**
     * Updates an existing user.
     *
     * @param id      the ID of the user to update
     * @param userDTO the user data transfer object
     * @return the updated user DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public AppUserDTO updateUser(UUID id, AppUserDTO userDTO) {
        logger.info("Updating user with ID: {}", id);
        AppUser existingUser = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + id));
        
        AppUser updatedUser = updateUserFields(existingUser, userDTO);
        updatedUser = appUserRepository.save(updatedUser);
        return userMapper.toDto(updatedUser);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public void deleteUser(UUID id) {
        logger.info("Deleting user with ID: {}", id);
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + id));
        appUserRepository.delete(user);
        logger.info("User deleted with ID: {}", id);
    }

    /**
     * Creates a new user via registration.
     *
     * @param registerRequest the registration request data transfer object
     * @return the created user DTO
     */
    @Transactional
    public AppUserDTO createUser(RegisterRequestDTO registerRequest) {
        logger.info("Creating new user via registration");
        return registrationService.registerUser(registerRequest);
    }

    private AppUser updateUserFields(AppUser user, AppUserDTO userDTO) {
        return user.toBuilder()
                .firstName(userDTO.firstName())
                .lastName(userDTO.lastName())
                .email(userDTO.email())
                .statusMessage(userDTO.statusMessage())
                .avatarUrl(userDTO.avatarUrl())
                .build();
    }
}