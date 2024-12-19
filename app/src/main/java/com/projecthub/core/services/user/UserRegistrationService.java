package com.projecthub.core.services.user;

import com.projecthub.core.dto.RegisterRequestDTO;
import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.models.AppUser;
import com.projecthub.core.exceptions.UserAlreadyExistsException;
import com.projecthub.core.mappers.AppUserMapper;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
import com.projecthub.core.services.auth.PasswordService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserRegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);

    private final AppUserJpaRepository userRepository;
    private final PasswordService passwordService;
    private final UserValidationService validationService;
    private final AppUserMapper userMapper;

    public UserRegistrationService(
            AppUserJpaRepository userRepository,
            PasswordService passwordService,
            UserValidationService validationService,
            AppUserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.validationService = validationService;
        this.userMapper = userMapper;
    }

    @Transactional
    public AppUserDTO registerUser(RegisterRequestDTO request) {
        logger.info("Processing registration for user: {}", request.getUsername());

        validationService.validateRegistration(request);
        checkUserAvailability(request);

        String encodedPassword = passwordService.encodePassword(request.getPassword());
        AppUser user = createUserEntity(request, encodedPassword);

        AppUser savedUser = userRepository.save(user);
        logger.info("Successfully registered user with ID: {}", savedUser.getId());

        return userMapper.toAppUserDTO(savedUser);
    }

    private void checkUserAvailability(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }
    }

    private AppUser createUserEntity(RegisterRequestDTO request, String encodedPassword) {
        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setCreatedAt(LocalDateTime.now());
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        return user;
    }
}
