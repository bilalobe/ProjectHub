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

    public EmailVerificationService(EmailService emailService, AppUserJpaRepository userRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void sendVerificationEmail(AppUser user) {
        logger.info("Sending verification email to user: {}", user.getEmail());

        String token = generateVerificationToken();
        AppUser updatedUser = user.toBuilder()
            .verificationToken(token)
            .verificationTokenExpiry(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS))
            .build();

        userRepository.save(updatedUser);

        Map<String, Object> templateVars = buildTemplateVariables(updatedUser, token);
        sendVerificationEmailTemplate(updatedUser, templateVars);
    }

    private Map<String, Object> buildTemplateVariables(AppUser user, String token) {
        return Map.of(
            "name", user.getFirstName(),
            "verificationLink", baseUrl + "/verify?token=" + token,
            "expiryHours", TOKEN_EXPIRY_HOURS
        );
    }

    private void sendVerificationEmailTemplate(AppUser user, Map<String, Object> templateVars) {
        emailService.sendTemplatedEmail(
            user.getEmail(),
            "Verify your email address",
            VERIFICATION_TEMPLATE,
            templateVars
        );
    }

    @Transactional
    public void verifyEmail(String token) throws InvalidTokenException {
        logger.info("Processing email verification token: {}", token);

        AppUser user = userRepository.findByVerificationToken(token)
            .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        validateVerification(user);

        AppUser verifiedUser = user.toBuilder()
            .verified(true)
            .enabled(true)
            .verificationToken(null)
            .verificationTokenExpiry(null)
            .build();

        userRepository.save(verifiedUser);
        logger.info("Email verified successfully for user: {}", verifiedUser.getEmail());
    }

    private void validateVerification(AppUser user) throws InvalidTokenException {
        if (user.isVerified()) {
            logger.warn("Attempted to verify already verified user: {}", user.getUsername());
            throw new InvalidTokenException("Email already verified");
        }

        if (user.getVerificationTokenExpiry() == null ||
            user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            logger.error("Verification token expired for user: {}", user.getUsername());
            throw new InvalidTokenException("Verification token has expired");
        }

        if (user.getVerificationToken() == null) {
            logger.error("No verification token found for user: {}", user.getUsername());
            throw new InvalidTokenException("Invalid verification token");
        }
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        logger.info("Resending verification email to: {}", email);

        AppUser user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (user.isVerified()) {
            logger.warn("Attempted to resend verification email for already verified user: {}", email);
            return;
        }

        AppUser refreshedUser = user.toBuilder()
            .verificationToken(generateVerificationToken())
            .verificationTokenExpiry(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS))
            .build();

        userRepository.save(refreshedUser);
        Map<String, Object> templateVars = buildTemplateVariables(refreshedUser, refreshedUser.getVerificationToken());
        sendVerificationEmailTemplate(refreshedUser, templateVars);
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    @Scheduled(cron = "0 0 2 * * *") // Run at 2 AM every day
    @Transactional
    public void cleanupExpiredTokens() {
        logger.info("Cleaning up expired verification tokens");
        LocalDateTime now = LocalDateTime.now();
        int deleted = userRepository.deleteExpiredVerificationTokens(now);
        logger.info("Cleaned up {} expired verification tokens", deleted);
    }
}
