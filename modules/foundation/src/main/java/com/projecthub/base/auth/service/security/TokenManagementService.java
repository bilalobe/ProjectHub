package com.projecthub.base.auth.service.security;

import com.projecthub.base.auth.domain.enums.TokenType;
import com.projecthub.base.auth.service.token.TokenFactory;
import com.projecthub.base.auth.service.token.TokenPair;
import com.projecthub.base.auth.service.token.TokenProperties;
import com.projecthub.base.repository.token.TokenRepository;
import com.projecthub.base.user.domain.entity.AppUser;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TokenManagementService {
    private final TokenFactory tokenFactory;
    private final TokenRepository tokenRepository;
    private final TokenProperties properties;
    private final SecurityEventPublisher eventPublisher;

    @Transactional
    public TokenPair generateTokenPair(AppUser user) {
        String accessToken = tokenFactory.createAccessToken(user);
        String refreshToken = tokenFactory.createRefreshToken(user);

        storeTokenPair(accessToken, refreshToken, user.getUsername());
        eventPublisher.publish(new TokenCreatedEvent(user.getId(), accessToken));

        return new TokenPair(
            accessToken,
            refreshToken,
            properties.accessTokenValidityMinutes() * 60L
        );
    }

    @Transactional
    public void revokeUserTokens(String username) {
        tokenRepository.revokeAllUserTokens(username);
        eventPublisher.publish(new TokenRevokedEvent(username));
        cleanupExpiredTokens();
    }

    private void storeTokenPair(String accessToken, String refreshToken, String username) {
        enforceMaximumTokenLimit(username);

        TokenEntity accessTokenEntity = createTokenEntity(accessToken, username, TokenType.ACCESS);
        TokenEntity refreshTokenEntity = createTokenEntity(refreshToken, username, TokenType.REFRESH);

        tokenRepository.saveAll(List.of(accessTokenEntity, refreshTokenEntity));
    }

    private void enforceMaximumTokenLimit(String username) {
        long activeTokenCount = tokenRepository.countActiveTokensByUsername(username);
        if (activeTokenCount >= properties.maxActiveTokens()) {
            tokenRepository.deleteOldestTokenForUser(username);
        }
    }

    @Scheduled(fixedRateString = "${app.security.token.cleanup-interval-ms}")
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    public String generateAccessToken(AppUser user) {
        return null;
    }
}
