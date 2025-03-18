package com.projecthub.auth.service.authentication.strategy;

import com.projecthub.auth.api.dto.LoginRequestDTO;
import com.projecthub.auth.service.PasswordService;
import com.projecthub.auth.service.authentication.AuthenticationStrategy;
import org.springframework.stereotype.Component;

/**
 * Default authentication strategy implementation using password-based authentication.
 */
@Component
public class DefaultAuthenticationStrategy implements AuthenticationStrategy {
    private final PasswordService passwordService;

    public DefaultAuthenticationStrategy(final PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Override
    public boolean authenticate(final String hashedPassword, final LoginRequestDTO request) {
        return this.passwordService.matches(request.password(), hashedPassword);
    }

    @Override
    public boolean supports(final LoginRequestDTO request) {
        return null == request.authenticationType() ||
            request.authenticationType().equals("password");
    }
}