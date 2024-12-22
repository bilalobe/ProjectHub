package com.projecthub.core.services.user;

import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.dto.RegisterRequestDTO;
import com.projecthub.core.entities.AppUser;
import com.projecthub.core.entities.Role;
import com.projecthub.core.events.UserRegisteredEvent;
import com.projecthub.core.events.UserVerifiedEvent;
import com.projecthub.core.exceptions.InvalidTokenException;
import com.projecthub.core.exceptions.RegistrationFailedException;
import com.projecthub.core.exceptions.UserAlreadyExistsException;
import com.projecthub.core.mappers.AppUserMapper;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
import com.projecthub.core.services.auth.PasswordService;
import com.projecthub.core.services.email.EmailVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class AppUserRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(AppUserRegistrationService.class);

    private final AppUserJpaRepository userRepository;
    private final PasswordService passwordService;
    private final AppUserValidationService validationService;
    private final AppUserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final RoleService roleService;
    private final EmailVerificationService emailVerificationService;

    public AppUserRegistrationService(
            AppUserJpaRepository userRepository,
            PasswordService passwordService,
            AppUserValidationService validationService,
            AppUserMapper userMapper,
            ApplicationEventPublisher eventPublisher,
            RoleService roleService,
            EmailVerificationService emailVerificationService) {

        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.validationService = validationService;
        this.userMapper = userMapper;
        this.eventPublisher = eventPublisher;
        this.roleService = roleService;
        this.emailVerificationService = emailVerificationService;
    }

    @Transactional
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public AppUserDTO registerUser(RegisterRequestDTO request) {
        logger.info("Starting registration process for user: {}", request.getUsername());

        // Validate request fields
        validationService.validateRegistration(request);
        checkUserAvailability(request);

        // Create the AppUser entity
        AppUser user = createUserEntity(request);

        // Persist in database
        user = userRepository.save(user);

        // Send verification email
        emailVerificationService.sendVerificationEmail(user);

        // Publish registration event
        eventPublisher.publishEvent(new UserRegisteredEvent(user));

        logger.info("Successfully registered user with ID: {}", user.getId());
        return userMapper.toAppUserDTO(user);
    }

    private void checkUserAvailability(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }
    }

    private AppUser createUserEntity(RegisterRequestDTO request) {
        Role userRole = roleService.getDefaultRole();
        String verificationToken = UUID.randomUUID().toString();
        return AppUser.builder()
                .username(request.getUsername())
                .password(passwordService.encodePassword(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .verificationToken(verificationToken)
                .verified(false)
                .enabled(false)
                .accountNonLocked(true)
                .failedAttempts(0)
                .lastLoginAttempt(null)
                .roles(Set.of(userRole))
                .build();
    }

    @Transactional
    public void verifyEmail(String token) throws InvalidTokenException {
        logger.info("Starting email verification process for token");

        // Find and validate user
        AppUser user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> {
                    logger.error("No user found with verification token");
                    return new InvalidTokenException("Invalid or expired verification token");
                });

        // Check if already verified
        if (user.isVerified()) {
            logger.warn("Attempted to verify already verified user: {}", user.getUsername());
            throw new InvalidTokenException("Email already verified");
        }

        // Check token expiry
        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            logger.error("Verification token expired for user: {}", user.getUsername());
            throw new InvalidTokenException("Verification token has expired");
        }

        // Update user verification status
        updateVerificationStatus(user);

        // Notify system of verification
        eventPublisher.publishEvent(new UserVerifiedEvent(user));

        logger.info("Email verification completed successfully for user: {}", user.getUsername());
    }

    private void updateVerificationStatus(AppUser user) {
        user.setVerified(true);
        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        logger.debug("Updated verification status for user: {}", user.getUsername());
    }

    @Recover
    public AppUserDTO handleRegistrationFailure(DataAccessException ex, RegisterRequestDTO request) {
        logger.error("All retry attempts failed for user registration: {}", request.getUsername(), ex);
        throw new RegistrationFailedException("Registration failed after multiple attempts", ex);
    }
}