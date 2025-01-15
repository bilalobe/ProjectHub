package com.projecthub.base.comm.application;

import com.projecthub.base.auth.domain.exception.InvalidTokenException;
import com.projecthub.base.repository.jpa.AppUserJpaRepository;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.user.domain.entity.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class EmailVerificationService {
    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationService.class);
    private static final String VERIFICATION_TEMPLATE = "email/verification";
    private static final int TOKEN_EXPIRY_HOURS = 24;

    private final EmailService emailService;
    private final AppUserJpaRepository userRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public EmailVerificationService(final EmailService emailService, final AppUserJpaRepository userRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void sendVerificationEmail(final AppUser user) {
        EmailVerificationService.logger.info("Sending verification email to user: {}", user.getEmail());

        final String token = this.generateVerificationToken();
        final AppUser updatedUser = user.toBuilder()
            .verificationToken(token)
            .verificationTokenExpiry(LocalDateTime.now().plusHours(EmailVerificationService.TOKEN_EXPIRY_HOURS))
            .build();

        this.userRepository.save(updatedUser);

        final Map<String, Object> templateVars = this.buildTemplateVariables(updatedUser, token);
        this.sendVerificationEmailTemplate(updatedUser, templateVars);
    }

    private Map<String, Object> buildTemplateVariables(final AppUser user, final String token) {
        return Map.of(
            "name", user.getFirstName(),
            "verificationLink", this.baseUrl + "/verify?token=" + token,
            "expiryHours", EmailVerificationService.TOKEN_EXPIRY_HOURS
        );
    }

    private void sendVerificationEmailTemplate(final AppUser user, final Map<String, Object> templateVars) {
        this.emailService.sendTemplatedEmail(
            user.getEmail(),
            "Verify your email address",
            EmailVerificationService.VERIFICATION_TEMPLATE,
            templateVars
        );
    }

    @Transactional
    public void verifyEmail(final String token) throws InvalidTokenException {
        EmailVerificationService.logger.info("Processing email verification token: {}", token);

        final AppUser user = this.userRepository.findByVerificationToken(token)
            .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        this.validateVerification(user);

        final AppUser verifiedUser = user.toBuilder()
            .verified(true)
            .enabled(true)
            .verificationToken(null)
            .verificationTokenExpiry(null)
            .build();

        this.userRepository.save(verifiedUser);
        EmailVerificationService.logger.info("Email verified successfully for user: {}", verifiedUser.getEmail());
    }

    private void validateVerification(final AppUser user) throws InvalidTokenException {
        if (user.isVerified()) {
            EmailVerificationService.logger.warn("Attempted to verify already verified user: {}", user.getUsername());
            throw new InvalidTokenException("Email already verified");
        }

        if (null == user.getVerificationTokenExpiry() ||
            user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            EmailVerificationService.logger.error("Verification token expired for user: {}", user.getUsername());
            throw new InvalidTokenException("Verification token has expired");
        }

        if (null == user.getVerificationToken()) {
            EmailVerificationService.logger.error("No verification token found for user: {}", user.getUsername());
            throw new InvalidTokenException("Invalid verification token");
        }
    }

    @Transactional
    public void resendVerificationEmail(final String email) {
        EmailVerificationService.logger.info("Resending verification email to: {}", email);

        final AppUser user = this.userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (user.isVerified()) {
            EmailVerificationService.logger.warn("Attempted to resend verification email for already verified user: {}", email);
            return;
        }

        final AppUser refreshedUser = user.toBuilder()
            .verificationToken(this.generateVerificationToken())
            .verificationTokenExpiry(LocalDateTime.now().plusHours(EmailVerificationService.TOKEN_EXPIRY_HOURS))
            .build();

        this.userRepository.save(refreshedUser);
        final Map<String, Object> templateVars = this.buildTemplateVariables(refreshedUser, refreshedUser.getVerificationToken());
        this.sendVerificationEmailTemplate(refreshedUser, templateVars);
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    @Scheduled(cron = "0 0 2 * * *") // Run at 2 AM every day
    @Transactional
    public void cleanupExpiredTokens() {
        EmailVerificationService.logger.info("Cleaning up expired verification tokens");
        final LocalDateTime now = LocalDateTime.now();
        final int deleted = this.userRepository.deleteExpiredVerificationTokens(now);
        EmailVerificationService.logger.info("Cleaned up {} expired verification tokens", deleted);
    }
}
