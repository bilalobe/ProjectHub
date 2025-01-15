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
    public TokenPair generateTokenPair(final AppUser user) {
        final String accessToken = this.tokenFactory.createAccessToken(user);
        final String refreshToken = this.tokenFactory.createRefreshToken(user);

        this.storeTokenPair(accessToken, refreshToken, user.getUsername());
        this.eventPublisher.publish(new TokenCreatedEvent(user.getId(), accessToken));

        return new TokenPair(
            accessToken,
            refreshToken,
            this.properties.accessTokenValidityMinutes() * 60L
        );
    }

    @Transactional
    public void revokeUserTokens(final String username) {
        this.tokenRepository.revokeAllUserTokens(username);
        this.eventPublisher.publish(new TokenRevokedEvent(username));
        this.cleanupExpiredTokens();
    }

    private void storeTokenPair(final String accessToken, final String refreshToken, final String username) {
        this.enforceMaximumTokenLimit(username);

        final TokenEntity accessTokenEntity = createTokenEntity(accessToken, username, TokenType.ACCESS);
        final TokenEntity refreshTokenEntity = createTokenEntity(refreshToken, username, TokenType.REFRESH);

        this.tokenRepository.saveAll(List.of(accessTokenEntity, refreshTokenEntity));
    }

    private void enforceMaximumTokenLimit(final String username) {
        final long activeTokenCount = this.tokenRepository.countActiveTokensByUsername(username);
        if (activeTokenCount >= this.properties.maxActiveTokens()) {
            this.tokenRepository.deleteOldestTokenForUser(username);
        }
    }

    @Scheduled(fixedRateString = "${app.security.token.cleanup-interval-ms}")
    public void cleanupExpiredTokens() {
        this.tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    public String generateAccessToken(final AppUser user) {
        return null;
    }
}
