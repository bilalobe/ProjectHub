package com.projecthub.base.auth.service.security;

import com.projecthub.base.auth.config.TokenConfig;
import com.projecthub.base.repository.token.TokenRepository;
import com.projecthub.base.shared.utils.JwtUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TokenValidationChain {
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final TokenConfig tokenConfig;

    public TokenValidationChain(final JwtUtil jwtUtil, final TokenRepository tokenRepository, final TokenConfig tokenConfig) {
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
        this.tokenConfig = tokenConfig;
    }

    public boolean validateAccessToken(final String token) {
        return this.validateBasicToken(token)
            && !this.isTokenBlacklisted(token)
            && this.isTokenActive(token)
            && this.validateTokenSignature(token);
    }

    public boolean validateRefreshToken(final String token) {
        return this.validateBasicToken(token)
            && !this.isTokenBlacklisted(token)
            && this.isTokenActive(token)
            && this.validateTokenSignature(token)
            && this.isRefreshTokenValid(token);
    }

    private boolean validateBasicToken(final String token) {
        return null != token && !token.isEmpty();
    }

    private boolean isTokenBlacklisted(final String token) {
        return this.tokenRepository.isTokenBlacklisted(token);
    }

    private boolean isTokenActive(final String token) {
        return this.tokenRepository.findByToken(token)
            .map(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
            .orElse(false);
    }

    private boolean validateTokenSignature(final String token) {
        try {
            return this.jwtUtil.validateToken(token);
        } catch (final Exception e) {
            return false;
        }
    }

    private boolean isRefreshTokenValid(final String token) {
        return this.tokenRepository.findByToken(token)
            .map(t -> t.getTokenType() == TokenType.REFRESH)
            .orElse(false);
    }
}
