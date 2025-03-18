package com.projecthub.base.auth.service.security;

import com.projecthub.base.auth.api.dto.AuthenticationResultDTO;
import com.projecthub.base.auth.api.dto.LoginRequestDTO;
import com.projecthub.base.auth.application.security.SecurityAuditService;
import com.projecthub.base.auth.domain.exception.AccountDisabledException;
import com.projecthub.base.auth.domain.exception.InvalidCredentialsException;
import com.projecthub.base.auth.domain.exception.SecurityExceptions.MfaRequiredException;
import com.projecthub.base.auth.domain.exception.SecurityExceptions.SuspiciousLocationException;
import com.projecthub.base.auth.service.PasswordService;
import com.projecthub.base.auth.service.RememberMeService;
import com.projecthub.base.repository.jpa.AppUserJpaRepository;
import com.projecthub.base.shared.domain.enums.security.SecurityAuditAction;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.user.api.dto.AppUserDTO;
import com.projecthub.base.user.domain.entity.AppUser;
import com.projecthub.base.user.domain.exception.AccountLockedException;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password";
    private static final int MAX_ATTEMPTS_WINDOW_MINUTES = 30;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int SUSPICIOUS_LOGIN_THRESHOLD = 3;

    private final AppUserJpaRepository userRepository;
    private final PasswordService passwordService;
    private final AccountLockingService lockingService;
    private final TokenManagementService tokenService;
    @NonNls
    private final SecurityAuditService auditService;
    private final RememberMeService rememberMeService;

    public AuthenticationService(
        AppUserJpaRepository userRepository,
        PasswordService passwordService,
        AccountLockingService lockingService,
        TokenManagementService tokenService,
        SecurityAuditService auditService,
        RememberMeService rememberMeService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.lockingService = lockingService;
        this.tokenService = tokenService;
        this.auditService = auditService;
        this.rememberMeService = rememberMeService;
    }

    @Transactional
    public AuthenticationResultDTO authenticate(LoginRequestDTO loginRequest) {
        logger.info("Authentication attempt for user: {}", loginRequest.principal());

        try {
            if (isRateLimited(loginRequest.principal(), loginRequest.ipAddress())) {
                throw new AccountLockedException("Too many login attempts. Please try again later.");
            }

            AppUser user = validateAndGetUser(loginRequest);
            validateSecurityStatus(user, loginRequest.ipAddress());

            String accessToken = TokenManagementService.generateAccessToken(user);
            AuthenticationResultDTO result = handleSuccessfulLogin(user, loginRequest, accessToken);

            auditService.logAuthenticationAttempt(user.getUsername(), true, loginRequest.ipAddress());
            return result;

        } catch (AccountLockedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            handleFailedLogin(loginRequest.principal(), loginRequest.ipAddress());
            throw e;
        }
    }

    private boolean isRateLimited(String username, String ipAddress) {
        int recentAttempts = SecurityAuditService.countRecentFailedAttempts(
            username,
            ipAddress,
            LocalDateTime.now().minusMinutes((long) MAX_ATTEMPTS_WINDOW_MINUTES)
        );
        return MAX_LOGIN_ATTEMPTS <= recentAttempts;
    }

    private void validateSecurityStatus(final AppUser user, final String ipAddress) {
        if (user.isRequiresMfaSetup()) {
            throw new MfaRequiredException("MFA setup required for this account");
        }

        if (user.isLocationSuspicious(ipAddress)) {
            throw new SuspiciousLocationException("Login attempt from suspicious location");
        }
    }

    private void handleFailedLogin(final String principal, final String ipAddress) {
        this.auditService.logAuthenticationAttempt(principal, false, ipAddress);
        if (this.isRateLimited(principal, ipAddress)) {
            this.auditService.logAccountAction(
                null,
                SecurityAuditAction.ACCOUNT_RATE_LIMITED,
                "Rate limit reached for: " + principal,
                ipAddress
            );
        }
    }

    private AppUser validateAndGetUser(final LoginRequestDTO loginRequest) {
        final AppUser user = this.userRepository.findByUsername(loginRequest.principal())
            .orElseThrow(() -> new InvalidCredentialsException(AuthenticationService.INVALID_CREDENTIALS_MESSAGE));

        AuthenticationService.validateUserStatus(user);
        this.validatePassword(user, loginRequest.password());

        if (0 < user.getFailedAttempts()) {
            this.lockingService.resetFailedAttempts(user);
        }

        return user;
    }

    private static void validateUserStatus(final AppUser user) {
        if (!user.isEnabled()) {
            throw new AccountDisabledException("Account is disabled");
        }
        if (!user.isAccountNonLocked()) {
            throw new AccountLockedException("Account is locked");
        }
    }

    private void validatePassword(final AppUser user, final String password) {
        try {
            validationService.validateCredentials(password, user.getPassword());
        } catch (final InvalidCredentialsException e) {
            this.lockingService.incrementFailedAttempts(user);
            throw e;
        }
    }

    private AuthenticationResultDTO handleSuccessfulLogin(final AppUser user, final LoginRequestDTO loginRequest, final String accessToken) {
        this.resetSecurityFlags(user);
        this.updateLoginMetadata(user, loginRequest.ipAddress());

        if (loginRequest.rememberMe()) {
            final RememberMeToken rememberMeToken = this.rememberMeService.createToken(user);
            return new AuthenticationResultDTO(accessToken, rememberMeToken.getTokenValue(), user.getId());
        }

        return new AuthenticationResultDTO(accessToken, null, user.getId());
    }

    private void resetSecurityFlags(final AppUser user) {
        user.setFailedAttempts(0);
        user.setLastLoginAttempt(LocalDateTime.now());
        if (user.isTemporarilyLocked()) {
            user.setLockExpiryTime(null);
        }
        this.userRepository.save(user);
    }

    private void updateLoginMetadata(final AppUser user, final String ipAddress) {
        user.setLastLoginIp(ipAddress);
        user.setLastLoginDate(LocalDateTime.now());
        if (!user.getKnownIpAddresses().contains(ipAddress)) {
            user.getKnownIpAddresses().add(ipAddress);
        }
        this.userRepository.save(user);
    }

    private static boolean isSuspiciousActivity(final AppUser user, final String ipAddress) {
        return !user.getKnownIpAddresses().contains(ipAddress) &&
            SUSPICIOUS_LOGIN_THRESHOLD <= user.getFailedAttempts();
    }

    @Transactional
    public void invalidateAllSessions(final UUID userId) {
        final AppUser user = this.userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        this.tokenService.revokeUserTokens(user.getUsername());
        this.rememberMeService.clearUserTokens(user.getUsername());
        user.setSecurityVersion(user.getSecurityVersion() + 1);
        this.userRepository.save(user);

        this.auditService.logAccountAction(
            userId,
            SecurityAuditAction.SESSIONS_INVALIDATED,
            "All sessions invalidated",
            null
        );
    }

    @Transactional
    public void logout(final String username) {
        AuthenticationService.logger.info("Logging out user: {}", username);
        this.rememberMeService.clearUserTokens(username);
        this.tokenService.revokeUserTokens(username);
        this.auditService.logAccountAction(
            this.userRepository.findByUsername(username)
                .map(AppUser::getId)
                .orElse(null),
            SecurityAuditAction.LOGOUT
        );
    }

    public static AppUserDTO getCurrentUser() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentUser'");
    }
}
