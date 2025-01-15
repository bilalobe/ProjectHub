package com.projecthub.base.user.application.mgmt.service;

import com.projecthub.base.auth.api.dto.RegisterRequestDTO;
import com.projecthub.base.auth.application.registration.AppUserRegistrationService;
import com.projecthub.base.repository.jpa.AppUserJpaRepository;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.user.api.dto.AppUserCredentialsDTO;
import com.projecthub.base.user.api.dto.AppUserDTO;
import com.projecthub.base.user.api.mapper.AppUserMapper;
import com.projecthub.base.user.domain.entity.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing application user.
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
        final AppUserJpaRepository appUserRepository,
        final AppUserMapper userMapper,
        final AppUserRegistrationService registrationService,
        final PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.userMapper = userMapper;
        this.registrationService = registrationService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves all user.
     *
     * @return a list of user DTOs
     */
    public List<AppUserDTO> getAlluser() {
        AppUserManagementService.logger.info("Retrieving all user");
        return this.appUserRepository.findAll().stream()
            .map(this.userMapper::toDto)
            .toList();
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the ID of the user to retrieve
     * @return the user DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    public AppUserDTO getUserById(final UUID id) {
        AppUserManagementService.logger.info("Retrieving user with ID: {}", id);
        final AppUser user = this.appUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AppUserManagementService.USER_NOT_FOUND + id));
        return this.userMapper.toDto(user);
    }

    /**
     * Saves a new user.
     *
     * @param userDTO the user data transfer object
     * @return the saved user DTO
     */
    @Transactional
    public AppUserDTO saveUser(final AppUserDTO userDTO) {
        AppUserManagementService.logger.info("Saving new user");

        // Encode the password using Spring Security's PasswordEncoder
        final String encodedPassword = this.passwordEncoder.encode(userDTO.password());

        final var credentials = new AppUserCredentialsDTO(userDTO.username(), encodedPassword);
        final var registerRequest = new RegisterRequestDTO(
            credentials,
            userDTO.email(),
            userDTO.username()
        );

        final AppUser user = this.userMapper.toEntity(registerRequest, userDTO.username());
        final AppUser savedUser = this.appUserRepository.save(user);
        return this.userMapper.toDto(savedUser);
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
    public AppUserDTO updateUser(final UUID id, final AppUserDTO userDTO) {
        AppUserManagementService.logger.info("Updating user with ID: {}", id);
        final AppUser existingUser = this.appUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AppUserManagementService.USER_NOT_FOUND + id));

        AppUser updatedUser = this.updateUserFields(existingUser, userDTO);
        updatedUser = this.appUserRepository.save(updatedUser);
        return this.userMapper.toDto(updatedUser);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public void deleteUser(final UUID id) {
        AppUserManagementService.logger.info("Deleting user with ID: {}", id);
        final AppUser user = this.appUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AppUserManagementService.USER_NOT_FOUND + id));
        this.appUserRepository.delete(user);
        AppUserManagementService.logger.info("User deleted with ID: {}", id);
    }

    /**
     * Creates a new user via registration.
     *
     * @param registerRequest the registration request data transfer object
     * @return the created user DTO
     */
    @Transactional
    public AppUserDTO createUser(final RegisterRequestDTO registerRequest) {
        AppUserManagementService.logger.info("Creating new user via registration");
        return this.registrationService.registerUser(registerRequest);
    }

    private AppUser updateUserFields(final AppUser user, final AppUserDTO userDTO) {
        return user.toBuilder()
            .firstName(userDTO.firstName())
            .lastName(userDTO.lastName())
            .email(userDTO.email())
            .statusMessage(userDTO.statusMessage())
            .avatarUrl(userDTO.avatarUrl())
            .build();
    }
}
