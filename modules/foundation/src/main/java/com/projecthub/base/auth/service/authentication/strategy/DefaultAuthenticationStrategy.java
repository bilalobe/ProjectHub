package com.projecthub.base.auth.service.authentication.strategy;

import com.projecthub.base.auth.api.dto.LoginRequestDTO;
import com.projecthub.base.auth.service.PasswordService;
import com.projecthub.base.auth.service.authentication.AuthenticationStrategy;
import com.projecthub.base.user.domain.entity.AppUser;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationStrategy implements AuthenticationStrategy {
    private final PasswordService passwordService;

    public DefaultAuthenticationStrategy(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Override
    public boolean authenticate(AppUser user, LoginRequestDTO request) {
        return passwordService.matches(request.password(), user.getPassword());
    }

    @Override
    public boolean supports(LoginRequestDTO request) {
        return request.authenticationType() == null ||
            request.authenticationType().equals("password");
    }
}
