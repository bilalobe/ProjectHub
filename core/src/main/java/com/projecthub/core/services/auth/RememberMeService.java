package com.projecthub.core.services.auth;

import com.projecthub.core.models.AppUser;
import com.projecthub.core.models.RememberMeToken;
import com.projecthub.core.repositories.RememberMeTokenRepository;
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

    public RememberMeService(RememberMeTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        this.secureRandom = new SecureRandom();
    }

    @Transactional
    public RememberMeToken createToken(AppUser user) {
        RememberMeToken token = new RememberMeToken();
        token.setUser(user);
        token.setTokenValue(generateTokenValue());
        token.setExpiryDate(LocalDateTime.now().plusDays(VALIDITY_DAYS));
        return tokenRepository.save(token);
    }

    @Transactional
    public void clearUserTokens(String username) {
        tokenRepository.deleteByUser_Username(username);
    }

    private String generateTokenValue() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getEncoder().encodeToString(tokenBytes);
    }
}
