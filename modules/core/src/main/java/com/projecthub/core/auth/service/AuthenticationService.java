package com.projecthub.core.auth.service;

import com.projecthub.core.dto.AuthenticationResult;
import com.projecthub.core.dto.LoginRequestDTO;
import com.projecthub.core.entities.AppUser;
import com.projecthub.core.entities.RememberMeToken;
import com.projecthub.core.enums.SecurityAuditAction;
import com.projecthub.core.exceptions.AccountDisabledException;
import com.projecthub.core.exceptions.AccountLockedException;
import com.projecthub.core.exceptions.InvalidCredentialsException;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.exceptions.SecurityExceptions.MfaRequiredException;
import com.projecthub.core.exceptions.SecurityExceptions.SuspiciousLocationException;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
import com.projecthub.core.services.audit.SecurityAuditService;
import com.projecthub.core.services.auth.AccountLockingService;
import com.projecthub.core.services.auth.PasswordService;
import com.projecthub.core.services.auth.RememberMeService;
import com.projecthub.core.services.auth.TokenManagementService;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public AuthenticationResult authenticate(LoginRequestDTO loginRequest) {
        logger.info("Authentication attempt for user: {}", loginRequest.principal());

        try {
            if (isRateLimited(loginRequest.principal(), loginRequest.ipAddress())) {
                throw new AccountLockedException("Too many login attempts. Please try again later.");
            }

            AppUser user = validateAndGetUser(loginRequest);
            validateSecurityStatus(user, loginRequest.ipAddress());
            
            String accessToken = tokenService.generateAccessToken(user);
            AuthenticationResult result = handleSuccessfulLogin(user, loginRequest, accessToken);
            
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
        if (!passwordService.matches(password, user.getPassword())) {
            lockingService.incrementFailedAttempts(user);
            throw new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }
    }

    private AuthenticationResult handleSuccessfulLogin(AppUser user, LoginRequestDTO loginRequest, String accessToken) {
        resetSecurityFlags(user);
        updateLoginMetadata(user, loginRequest.ipAddress());
        
        if (loginRequest.rememberMe()) {
            RememberMeToken rememberMeToken = rememberMeService.createToken(user);
            return new AuthenticationResult(accessToken, rememberMeToken.getTokenValue(), user.getId());
        }
        
        return new AuthenticationResult(accessToken, null, user.getId());
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
}