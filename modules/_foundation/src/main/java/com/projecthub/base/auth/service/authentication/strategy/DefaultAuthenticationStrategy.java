package com.projecthub.base.auth.service.authentication.strategy;

import com.projecthub.base.auth.api.dto.LoginRequestDTO;
import com.projecthub.base.auth.service.PasswordService;
import com.projecthub.base.auth.service.authentication.AuthenticationStrategy;
import com.projecthub.base.user.domain.entity.AppUser;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationStrategy implements AuthenticationStrategy {
    private final PasswordService passwordService;

    public DefaultAuthenticationStrategy(final PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Override
    public boolean authenticate(final AppUser user, final LoginRequestDTO request) {
        return this.passwordService.matches(request.password(), user.getPassword());
    }

    @Override
    public boolean supports(final LoginRequestDTO request) {
        return null == request.authenticationType() ||
            request.authenticationType().equals("password");
    }
}
