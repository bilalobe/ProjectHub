package com.projecthub.core.services.registration;

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
import com.projecthub.core.services.email.EmailVerificationService;
import com.projecthub.core.services.user.AppUserValidationService;
import com.projecthub.core.services.user.AppUserRoleService;
import com.projecthub.core.services.validation.AppUserValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class AppUserRegistrationService implements UserRegistration {

    private static final Logger logger = LoggerFactory.getLogger(AppUserRegistrationService.class);

    private final AppUserJpaRepository userRepository;
    private final AppUserValidationService validationService;
    private final AppUserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final AppUserRoleService roleService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordEncoder passwordEncoder;
    private final AppUserValidation validation;

    public AppUserRegistrationService(
            AppUserJpaRepository userRepository,
            AppUserValidationService validationService,
            AppUserMapper userMapper,
            ApplicationEventPublisher eventPublisher,
            AppUserRoleService roleService,
            EmailVerificationService emailVerificationService,
            PasswordEncoder passwordEncoder,
            AppUserValidation validation) {

        this.userRepository = userRepository;
        this.validationService = validationService;
        this.userMapper = userMapper;
        this.eventPublisher = eventPublisher;
        this.roleService = roleService;
        this.emailVerificationService = emailVerificationService;
        this.passwordEncoder = passwordEncoder;
        this.validation = validation;
    }

    @Override
    @Transactional
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public AppUserDTO registerUser(RegisterRequestDTO request) {
        logger.info("Starting registration process for user: {}", request.username());
        validation.validateForRegistration(request);

        AppUser user = createUserEntity(request);
        user = userRepository.save(user);

        emailVerificationService.sendVerificationEmail(user);
        eventPublisher.publishEvent(new UserRegisteredEvent(user));

        logger.info("Successfully registered user with ID: {}", user.getId());
        return userMapper.toDto(user);
    }

    private void checkUserAvailability(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.email());
        }
    }

    private AppUser createUserEntity(RegisterRequestDTO request) {
        Role userRole = roleService.getDefaultRole();
        String verificationToken = UUID.randomUUID().toString();

        return AppUser.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.credentials().password()))
                .email(request.email())
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
        logger.error("All retry attempts failed for user registration: {}", request.username(), ex);
        throw new RegistrationFailedException("Registration failed after multiple attempts", ex);
    }

    @Override
    public void resendVerification(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resendVerification'");
    }
}