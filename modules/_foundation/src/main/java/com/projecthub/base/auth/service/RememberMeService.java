package com.projecthub.base.auth.service;

import com.projecthub.base.auth.domain.entity.RememberMeToken;
import com.projecthub.base.auth.infrastructure.persistence.token.RememberMeTokenRepository;
import com.projecthub.base.user.domain.entity.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class RememberMeService {
    private static final int TOKEN_LENGTH = 32;
    private static final int VALIDITY_DAYS = 30;
    private final RememberMeTokenRepository tokenRepository;
    private final SecureRandom secureRandom;

    public RememberMeService(final RememberMeTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        secureRandom = new SecureRandom();
    }

    @Transactional
    public RememberMeToken createToken(final AppUser user) {
        final RememberMeToken token = new RememberMeToken();
        token.setUser(user);
        token.setTokenValue(this.generateTokenValue());
        token.setExpiryDate(LocalDateTime.now().plusDays((long) RememberMeService.VALIDITY_DAYS));
        return this.tokenRepository.save(token);
    }

    @Transactional
    public void clearUserTokens(final String username) {
        this.tokenRepository.deleteByUser_Username(username);
    }

    private String generateTokenValue() {
        final byte[] tokenBytes = new byte[RememberMeService.TOKEN_LENGTH];
        this.secureRandom.nextBytes(tokenBytes);
        return Base64.getEncoder().encodeToString(tokenBytes);
    }
}
