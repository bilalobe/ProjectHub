package com.projecthub.core.services.auth;

import com.projecthub.core.dto.AuthenticationResult;
import com.projecthub.core.dto.LoginRequestDTO;
import com.projecthub.core.entities.AppUser;
import com.projecthub.core.entities.RememberMeToken;
import com.projecthub.core.enums.SecurityAuditAction;
import com.projecthub.core.exceptions.AccountDisabledException;
import com.projecthub.core.exceptions.AccountLockedException;
import com.projecthub.core.exceptions.InvalidCredentialsException;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
import com.projecthub.core.services.audit.SecurityAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password";

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
        logger.info("Authenticating user: {}", loginRequest.getUsername());

        try {
            AppUser user = validateAndGetUser(loginRequest);
            String accessToken = tokenService.generateAccessToken(user);

            if (loginRequest.isRememberMe()) {
                RememberMeToken rememberMeToken = rememberMeService.createToken(user);
                return new AuthenticationResult(accessToken, user.getId(), rememberMeToken.getTokenValue());
            }

            auditService.logAuthenticationAttempt(user.getUsername(), true, loginRequest.getIpAddress());
            return new AuthenticationResult();

        } catch (Exception e) {
            auditService.logAuthenticationAttempt(loginRequest.getUsername(), false, loginRequest.getIpAddress());
            throw e;
        }
    }

    private AppUser validateAndGetUser(LoginRequestDTO loginRequest) {
        AppUser user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE));

        validateUserStatus(user);
        validatePassword(user, loginRequest.getPassword());

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