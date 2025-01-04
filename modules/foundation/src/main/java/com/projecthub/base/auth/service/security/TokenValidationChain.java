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

    public TokenValidationChain(JwtUtil jwtUtil, TokenRepository tokenRepository, TokenConfig tokenConfig) {
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
        this.tokenConfig = tokenConfig;
    }

    public boolean validateAccessToken(String token) {
        return validateBasicToken(token)
            && !isTokenBlacklisted(token)
            && isTokenActive(token)
            && validateTokenSignature(token);
    }

    public boolean validateRefreshToken(String token) {
        return validateBasicToken(token)
            && !isTokenBlacklisted(token)
            && isTokenActive(token)
            && validateTokenSignature(token)
            && isRefreshTokenValid(token);
    }

    private boolean validateBasicToken(String token) {
        return token != null && !token.isEmpty();
    }

    private boolean isTokenBlacklisted(String token) {
        return tokenRepository.isTokenBlacklisted(token);
    }

    private boolean isTokenActive(String token) {
        return tokenRepository.findByToken(token)
            .map(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
            .orElse(false);
    }

    private boolean validateTokenSignature(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isRefreshTokenValid(String token) {
        return tokenRepository.findByToken(token)
            .map(t -> t.getTokenType() == TokenType.REFRESH)
            .orElse(false);
    }
}
