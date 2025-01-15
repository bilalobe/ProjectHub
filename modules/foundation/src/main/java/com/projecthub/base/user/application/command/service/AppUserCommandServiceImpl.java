package com.projecthub.base.user.application.command.service;

import com.projecthub.base.auth.api.dto.RegisterRequestDTO;
import com.projecthub.base.auth.application.registration.AppUserRegistrationService;
import com.projecthub.base.repository.jpa.AppUserJpaRepository;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.user.api.dto.AppUserCredentialsDTO;
import com.projecthub.base.user.api.dto.AppUserDTO;
import com.projecthub.base.user.api.dto.CreateUserDTO;
import com.projecthub.base.user.api.mapper.AppUserMapper;
import com.projecthub.base.user.domain.entity.AppUser;
import com.projecthub.base.user.domain.validation.AppUserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AppUserCommandServiceImpl implements AppUserCommandService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserCommandServiceImpl.class);
    private static final String USER_NOT_FOUND = "User not found with ID: ";

    private final AppUserJpaRepository appUserRepository;
    private final AppUserMapper userMapper;
    private final AppUserValidator validation;
    private final AppUserRegistrationService registrationService;

    public AppUserCommandServiceImpl(
        final AppUserJpaRepository appUserRepository,
        final AppUserMapper userMapper,
        final AppUserValidator validation,
        final AppUserRegistrationService registrationService) {
        this.appUserRepository = appUserRepository;
        this.userMapper = userMapper;
        this.validation = validation;
        this.registrationService = registrationService;
    }

    @Override
    @Transactional
    public AppUserDTO createUser(final CreateUserDTO createUserDTO) {
        AppUserCommandServiceImpl.logger.info("Creating new user with username: {}", createUserDTO.username());
        this.validation.validateForCreation(createUserDTO);
        final RegisterRequestDTO registerRequest = this.convertToRegisterRequest(createUserDTO);
        return this.registrationService.registerUser(registerRequest);
    }

    @Override
    @Transactional
    public AppUserDTO updateUser(final UUID id, final AppUserDTO userDTO) {
        AppUserCommandServiceImpl.logger.info("Updating user with ID: {}", id);
        this.validation.validateForUpdate(userDTO);

        final AppUser existingUser = this.appUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AppUserCommandServiceImpl.USER_NOT_FOUND + id));

        AppUser updatedUser = this.updateUserFields(existingUser, userDTO);
        updatedUser = this.appUserRepository.save(updatedUser);
        return this.userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(final UUID id) {
        AppUserCommandServiceImpl.logger.info("Deleting user with ID: {}", id);
        final AppUser user = this.appUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AppUserCommandServiceImpl.USER_NOT_FOUND + id));
        this.appUserRepository.delete(user);
        AppUserCommandServiceImpl.logger.info("User deleted with ID: {}", id);
    }

    private RegisterRequestDTO convertToRegisterRequest(final CreateUserDTO userDTO) {
        return new RegisterRequestDTO(
            new AppUserCredentialsDTO(userDTO.username(), userDTO.password()),
            userDTO.email(),
            userDTO.username());
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
