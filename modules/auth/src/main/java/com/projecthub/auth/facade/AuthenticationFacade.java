package com.projecthub.auth.facade;

import com.projecthub.auth.dto.AuthenticationResponse;
import com.projecthub.auth.dto.TokenRefreshRequest;

public interface AuthenticationFacade {
    AuthenticationResponse authenticate(String username, String password);
    AuthenticationResponse refreshToken(TokenRefreshRequest refreshRequest);
    void logout();
    boolean isCurrentUserHasRole(String role);
    boolean isCurrentUserHasPermission(String permission);
}