package com.projecthub.base.auth.service.authentication;

import com.projecthub.base.auth.api.dto.LoginRequestDTO;
import com.projecthub.base.user.domain.entity.AppUser;

public interface AuthenticationStrategy {
    boolean authenticate(AppUser user, LoginRequestDTO request);

    boolean supports(LoginRequestDTO request);
}
