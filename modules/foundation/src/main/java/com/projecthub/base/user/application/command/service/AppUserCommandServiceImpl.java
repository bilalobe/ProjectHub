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
        AppUserJpaRepository appUserRepository,
        AppUserMapper userMapper,
        AppUserValidator validation,
        AppUserRegistrationService registrationService) {
        this.appUserRepository = appUserRepository;
        this.userMapper = userMapper;
        this.validation = validation;
        this.registrationService = registrationService;
    }

    @Override
    @Transactional
    public AppUserDTO createUser(CreateUserDTO createUserDTO) {
        logger.info("Creating new user with username: {}", createUserDTO.username());
        validation.validateForCreation(createUserDTO);
        RegisterRequestDTO registerRequest = convertToRegisterRequest(createUserDTO);
        return registrationService.registerUser(registerRequest);
    }

    @Override
    @Transactional
    public AppUserDTO updateUser(UUID id, AppUserDTO userDTO) {
        logger.info("Updating user with ID: {}", id);
        validation.validateForUpdate(userDTO);

        AppUser existingUser = appUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + id));

        AppUser updatedUser = updateUserFields(existingUser, userDTO);
        updatedUser = appUserRepository.save(updatedUser);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        logger.info("Deleting user with ID: {}", id);
        AppUser user = appUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + id));
        appUserRepository.delete(user);
        logger.info("User deleted with ID: {}", id);
    }

    private RegisterRequestDTO convertToRegisterRequest(CreateUserDTO userDTO) {
        return new RegisterRequestDTO(
            new AppUserCredentialsDTO(userDTO.username(), userDTO.password()),
            userDTO.email(),
            userDTO.username());
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
