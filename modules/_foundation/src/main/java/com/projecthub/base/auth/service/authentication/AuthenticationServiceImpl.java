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
    private final ApplicationEventPublisher eventPublisher;
    private final List<AuthenticationStrategy> authenticationStrategies;

    public AuthenticationServiceImpl(
        // ...existing parameters...,
        final ApplicationEventPublisher eventPublisher,
        final List<AuthenticationStrategy> authenticationStrategies) {
        // ...existing assignments...
        this.eventPublisher = eventPublisher;
        this.authenticationStrategies = authenticationStrategies;
    }

    @Override
    @Transactional
    public AuthenticationResultDTO authenticate(final LoginRequestDTO loginRequest) {
        try {
            final AppUser user = this.validateAndGetUser(loginRequest);
            final AuthenticationStrategy strategy = this.findAuthenticationStrategy(loginRequest);

            if (!strategy.authenticate(user, loginRequest)) {
                this.handleFailedLogin(loginRequest);
                throw new InvalidCredentialsException(AuthenticationService.INVALID_CREDENTIALS_MESSAGE);
            }

            final AuthenticationResultDTO result = this.handleSuccessfulLogin(user, loginRequest);
            this.publishAuthenticationEvent(AuthenticationEvent.createLoginSuccess(
                user.getId(), user.getUsername(), loginRequest.ipAddress()));

            return result;
        } catch (InvalidCredentialsException e) {
            throw new RuntimeException(e);
        } catch (final Exception e) {
            this.handleFailedLogin(loginRequest);
            this.publishAuthenticationEvent(AuthenticationEvent.createLoginFailure(
                loginRequest.principal(), loginRequest.ipAddress(), e.getMessage()));
            throw e;
        }
    }

    private AuthenticationStrategy findAuthenticationStrategy(final LoginRequestDTO request) {
        return this.authenticationStrategies.stream()
            .filter(strategy -> strategy.supports(request))
            .findFirst()
            .orElseThrow(() -> new UnsupportedAuthenticationTypeException(
                "No authentication strategy found for: " + request.authenticationType()));
    }

    private void publishAuthenticationEvent(final AuthenticationEvent event) {
        this.eventPublisher.publishEvent(event);
    }

    // ...existing methods...
}
