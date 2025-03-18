package com.projecthub.base.auth.application.registration;

import com.projecthub.base.auth.api.dto.RegisterRequestDTO;
import com.projecthub.base.auth.domain.entity.Role;
import com.projecthub.base.auth.domain.event.UserRegisteredEvent;
import com.projecthub.base.auth.domain.event.UserVerifiedEvent;
import com.projecthub.base.auth.domain.exception.InvalidTokenException;
import com.projecthub.base.comm.application.EmailVerificationService;
import com.projecthub.base.user.api.dto.AppUserDTO;
import com.projecthub.base.user.api.mapper.AppUserMapper;
import com.projecthub.base.user.api.validation.AppUserValidationService;
import com.projecthub.base.user.application.role.service.AppUserRoleService;
import com.projecthub.base.user.domain.entity.AppUser;
import com.projecthub.base.user.domain.exception.RegistrationFailedException;
import com.projecthub.base.user.domain.exception.UserAlreadyExistsException;
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
        final AppUserJpaRepository userRepository,
        final AppUserValidationService validationService,
        final AppUserMapper userMapper,
        final ApplicationEventPublisher eventPublisher,
        final AppUserRoleService roleService,
        final EmailVerificationService emailVerificationService,
        final PasswordEncoder passwordEncoder,
        final AppUserValidation validation) {

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
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000L))
    public AppUserDTO registerUser(final RegisterRequestDTO request) {
        AppUserRegistrationService.logger.info("Starting registration process for user: {}", request.username());
        this.validation.validateForRegistration(request);

        AppUser user = this.createUserEntity(request);
        user = this.userRepository.save(user);

        this.emailVerificationService.sendVerificationEmail(user);
        this.eventPublisher.publishEvent(new UserRegisteredEvent(user));

        AppUserRegistrationService.logger.info("Successfully registered user with ID: {}", user.getId());
        return this.userMapper.toDto(user);
    }

    private void checkUserAvailability(final RegisterRequestDTO request) {
        if (this.userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.username());
        }
        if (this.userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.email());
        }
    }

    private AppUser createUserEntity(final RegisterRequestDTO request) {
        final Role userRole = this.roleService.getDefaultRole();
        final String verificationToken = UUID.randomUUID().toString();

        return AppUser.builder()
            .username(request.username())
            .password(this.passwordEncoder.encode(request.credentials().password()))
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

    /**
     * @param token
     * @throws InvalidTokenException
     */
    @Transactional
    public void verifyEmail(final String token) {
        AppUserRegistrationService.logger.info("Starting email verification process for token");

        // Find and validate user
        final AppUser user = this.userRepository.findByVerificationToken(token)
            .orElseThrow(() -> {
                AppUserRegistrationService.logger.error("No user found with verification token");
                return new InvalidTokenException("Invalid or expired verification token");
            });

        // Check if already verified
        if (user.isVerified()) {
            AppUserRegistrationService.logger.warn("Attempted to verify already verified user: {}", user.getUsername());
            throw new InvalidTokenException("Email already verified");
        }

        // Check token expiry
        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            AppUserRegistrationService.logger.error("Verification token expired for user: {}", user.getUsername());
            throw new InvalidTokenException("Verification token has expired");
        }

        // Update user verification status
        this.updateVerificationStatus(user);

        // Notify system of verification
        this.eventPublisher.publishEvent(new UserVerifiedEvent(user));

        AppUserRegistrationService.logger.info("Email verification completed successfully for user: {}", user.getUsername());
    }

    private void updateVerificationStatus(final AppUser user) {
        user.setVerified(true);
        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        this.userRepository.save(user);
        AppUserRegistrationService.logger.debug("Updated verification status for user: {}", user.getUsername());
    }

    @Recover
    public static AppUserDTO handleRegistrationFailure(final DataAccessException ex, final RegisterRequestDTO request) {
        AppUserRegistrationService.logger.error("All retry attempts failed for user registration: {}", request.username(), ex);
        throw new RegistrationFailedException("Registration failed after multiple attempts", ex);
    }

    @Override
    public void resendVerification(final String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resendVerification'");
    }
}
