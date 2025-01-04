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

            String accessToken = tokenService.generateAccessToken(user);
            AuthenticationResultDTO result = handleSuccessfulLogin(user, loginRequest, accessToken);

            auditService.logAuthenticationAttempt(user.getUsername(), true, loginRequest.ipAddress());
            return result;

        } catch (Exception e) {
            handleFailedLogin(loginRequest.principal(), loginRequest.ipAddress());
            throw e;
        }
    }

    private boolean isRateLimited(String username, String ipAddress) {
        int recentAttempts = auditService.countRecentFailedAttempts(
            username,
            ipAddress,
            LocalDateTime.now().minusMinutes(MAX_ATTEMPTS_WINDOW_MINUTES)
        );
        return recentAttempts >= MAX_LOGIN_ATTEMPTS;
    }

    private void validateSecurityStatus(AppUser user, String ipAddress) {
        if (user.isRequiresMfaSetup()) {
            throw new MfaRequiredException("MFA setup required for this account");
        }

        if (user.isLocationSuspicious(ipAddress)) {
            throw new SuspiciousLocationException("Login attempt from suspicious location");
        }
    }

    private void handleFailedLogin(String principal, String ipAddress) {
        auditService.logAuthenticationAttempt(principal, false, ipAddress);
        if (isRateLimited(principal, ipAddress)) {
            auditService.logAccountAction(
                null,
                SecurityAuditAction.ACCOUNT_RATE_LIMITED,
                "Rate limit reached for: " + principal,
                ipAddress
            );
        }
    }

    private AppUser validateAndGetUser(LoginRequestDTO loginRequest) {
        AppUser user = userRepository.findByUsername(loginRequest.principal())
            .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE));

        validateUserStatus(user);
        validatePassword(user, loginRequest.password());

        if (user.getFailedAttempts() > 0) {
            lockingService.resetFailedAttempts(user);
        }

        return user;
    }

    private void validateUserStatus(AppUser user) {
        if (!user.isEnabled()) {
            throw new AccountDisabledException("Account is disabled");
        }
        if (!user.isAccountNonLocked()) {
            throw new AccountLockedException("Account is locked");
        }
    }

    private void validatePassword(AppUser user, String password) {
        try {
            validationService.validateCredentials(password, user.getPassword());
        } catch (InvalidCredentialsException e) {
            lockingService.incrementFailedAttempts(user);
            throw e;
        }
    }

    private AuthenticationResultDTO handleSuccessfulLogin(AppUser user, LoginRequestDTO loginRequest, String accessToken) {
        resetSecurityFlags(user);
        updateLoginMetadata(user, loginRequest.ipAddress());

        if (loginRequest.rememberMe()) {
            RememberMeToken rememberMeToken = rememberMeService.createToken(user);
            return new AuthenticationResultDTO(accessToken, rememberMeToken.getTokenValue(), user.getId());
        }

        return new AuthenticationResultDTO(accessToken, null, user.getId());
    }

    private void resetSecurityFlags(AppUser user) {
        user.setFailedAttempts(0);
        user.setLastLoginAttempt(LocalDateTime.now());
        if (user.isTemporarilyLocked()) {
            user.setLockExpiryTime(null);
        }
        userRepository.save(user);
    }

    private void updateLoginMetadata(AppUser user, String ipAddress) {
        user.setLastLoginIp(ipAddress);
        user.setLastLoginDate(LocalDateTime.now());
        if (!user.getKnownIpAddresses().contains(ipAddress)) {
            user.getKnownIpAddresses().add(ipAddress);
        }
        userRepository.save(user);
    }

    private boolean isSuspiciousActivity(AppUser user, String ipAddress) {
        return !user.getKnownIpAddresses().contains(ipAddress) &&
            user.getFailedAttempts() >= SUSPICIOUS_LOGIN_THRESHOLD;
    }

    @Transactional
    public void invalidateAllSessions(UUID userId) {
        AppUser user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        tokenService.revokeUserTokens(user.getUsername());
        rememberMeService.clearUserTokens(user.getUsername());
        user.setSecurityVersion(user.getSecurityVersion() + 1);
        userRepository.save(user);

        auditService.logAccountAction(
            userId,
            SecurityAuditAction.SESSIONS_INVALIDATED,
            "All sessions invalidated",
            null
        );
    }

    @Transactional
    public void logout(String username) {
        logger.info("Logging out user: {}", username);
        rememberMeService.clearUserTokens(username);
        tokenService.revokeUserTokens(username);
        auditService.logAccountAction(
            userRepository.findByUsername(username)
                .map(AppUser::getId)
                .orElse(null),
            SecurityAuditAction.LOGOUT
        );
    }

    public AppUserDTO getCurrentUser() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentUser'");
    }
}
