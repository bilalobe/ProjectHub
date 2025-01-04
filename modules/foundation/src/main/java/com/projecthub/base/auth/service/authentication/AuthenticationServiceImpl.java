package com.projecthub.base.auth.service.authentication;

import com.projecthub.base.auth.api.dto.AuthenticationResultDTO;
import com.projecthub.base.auth.api.dto.LoginRequestDTO;
import com.projecthub.base.auth.domain.event.AuthenticationEvent;
import com.projecthub.base.auth.domain.exception.InvalidCredentialsException;
import com.projecthub.base.auth.service.security.AuthenticationService;
import com.projecthub.base.user.domain.entity.AppUser;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    // ...existing fields...
    private final ApplicationEventPublisher eventPublisher;
    private final List<AuthenticationStrategy> authenticationStrategies;

    public AuthenticationServiceImpl(
        // ...existing parameters...,
        ApplicationEventPublisher eventPublisher,
        List<AuthenticationStrategy> authenticationStrategies) {
        // ...existing assignments...
        this.eventPublisher = eventPublisher;
        this.authenticationStrategies = authenticationStrategies;
    }

    @Override
    @Transactional
    public AuthenticationResultDTO authenticate(LoginRequestDTO loginRequest) {
        try {
            AppUser user = validateAndGetUser(loginRequest);
            AuthenticationStrategy strategy = findAuthenticationStrategy(loginRequest);

            if (!strategy.authenticate(user, loginRequest)) {
                handleFailedLogin(loginRequest);
                throw new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE);
            }

            AuthenticationResultDTO result = handleSuccessfulLogin(user, loginRequest);
            publishAuthenticationEvent(AuthenticationEvent.createLoginSuccess(
                user.getId(), user.getUsername(), loginRequest.ipAddress()));

            return result;
        } catch (Exception e) {
            handleFailedLogin(loginRequest);
            publishAuthenticationEvent(AuthenticationEvent.createLoginFailure(
                loginRequest.principal(), loginRequest.ipAddress(), e.getMessage()));
            throw e;
        }
    }

    private AuthenticationStrategy findAuthenticationStrategy(LoginRequestDTO request) {
        return authenticationStrategies.stream()
            .filter(strategy -> strategy.supports(request))
            .findFirst()
            .orElseThrow(() -> new UnsupportedAuthenticationTypeException(
                "No authentication strategy found for: " + request.authenticationType()));
    }

    private void publishAuthenticationEvent(AuthenticationEvent event) {
        eventPublisher.publishEvent(event);
    }

    // ...existing methods...
}
