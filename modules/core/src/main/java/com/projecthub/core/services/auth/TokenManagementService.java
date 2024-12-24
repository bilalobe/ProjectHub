package com.projecthub.core.services.auth;

import com.projecthub.core.entities.AppUser;
import com.projecthub.core.repositories.TokenRepository;
import com.projecthub.core.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TokenManagementService {
    private static final Logger logger = LoggerFactory.getLogger(TokenManagementService.class);
    private static final int TOKEN_EXPIRY_DAYS = 7;
    private static final int MAX_ACTIVE_TOKENS = 5;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    public TokenManagementService(JwtUtil jwtUtil, TokenRepository tokenRepository) {
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
    }

    public String generateAccessToken(AppUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toList()));
        claims.put("userId", user.getId().toString());
        
        return jwtUtil.generateToken(user.getUsername(), claims);
    }

    @Transactional
    public void revokeUserTokens(String username) {
        logger.info("Revoking all tokens for user: {}", username);
        tokenRepository.revokeAllUserTokens(username);
        tokenRepository.deleteExpiredTokens(LocalDateTime.now().minusDays(TOKEN_EXPIRY_DAYS));
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public boolean validateTokenForUser(String token, String username) {
        if (!tokenRepository.isTokenValid(token)) {
            logger.warn("Token validation failed - token is revoked or blacklisted");
            return false;
        }
        
        try {
            boolean isValid = jwtUtil.validateToken(token, username);
            if (!isValid) {
                logger.warn("Token validation failed for user: {}", username);
                tokenRepository.blacklistToken(token);
            }
            return isValid;
        } catch (Exception e) {
            logger.error("Token validation error", e);
            tokenRepository.blacklistToken(token);
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }

    @Transactional
    public void storeToken(String token, String username) {
        cleanupExpiredTokens();
        enforceMaximumTokenLimit(username);
        
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken(token);
        tokenEntity.setUsername(username);
        tokenEntity.setCreatedAt(LocalDateTime.now());
        tokenEntity.setExpiresAt(LocalDateTime.now().plusDays(TOKEN_EXPIRY_DAYS));
        
        tokenRepository.save(tokenEntity);
    }

    private void enforceMaximumTokenLimit(String username) {
        long activeTokenCount = tokenRepository.countActiveTokensByUsername(username);
        if (activeTokenCount >= MAX_ACTIVE_TOKENS) {
            tokenRepository.deleteOldestTokenForUser(username);
        }
    }

    private void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenRepository.isTokenBlacklisted(token);
    }
}
