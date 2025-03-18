package com.projecthub.auth.facade;

import com.projecthub.auth.dto.AuthenticationResponse;
import com.projecthub.auth.dto.TokenRefreshRequest;
import com.projecthub.auth.service.AuthenticationService;
import com.projecthub.auth.service.TokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationFacade implements AuthenticationFacade {
    private final AuthenticationService authenticationService;
    private final TokenService tokenService;

    public DefaultAuthenticationFacade(AuthenticationService authenticationService, TokenService tokenService) {
        this.authenticationService = authenticationService;
        this.tokenService = tokenService;
    }

    @Override
    public AuthenticationResponse authenticate(String username, String password) {
        Authentication authentication = authenticationService.authenticate(username, password);
        String accessToken = tokenService.generateAccessToken(authentication);
        String refreshToken = tokenService.generateRefreshToken(authentication);
        
        return new AuthenticationResponse(
            accessToken,
            refreshToken,
            3600, // 1 hour default expiration
            username
        );
    }

    @Override
    public AuthenticationResponse refreshToken(TokenRefreshRequest refreshRequest) {
        String oldRefreshToken = refreshRequest.getRefreshToken();
        if (!tokenService.validateToken(oldRefreshToken)) {
            throw new com.projecthub.auth.exception.AuthenticationException("Invalid refresh token");
        }

        Authentication authentication = tokenService.getAuthentication(oldRefreshToken);
        String newAccessToken = tokenService.generateAccessToken(authentication);
        String newRefreshToken = tokenService.generateRefreshToken(authentication);

        return new AuthenticationResponse(
            newAccessToken,
            newRefreshToken,
            3600,
            authentication.getName()
        );
    }

    @Override
    public void logout() {
        authenticationService.logout(authenticationService.getCurrentAuthentication().getName());
    }

    @Override
    public boolean isCurrentUserHasRole(String role) {
        Authentication authentication = authenticationService.getCurrentAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(authority -> authority.equals("ROLE_" + role.toUpperCase()));
    }

    @Override
    public boolean isCurrentUserHasPermission(String permission) {
        Authentication authentication = authenticationService.getCurrentAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(authority -> authority.equals(permission));
    }
}