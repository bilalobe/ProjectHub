package com.projecthub.core.services.auth;

import com.projecthub.core.entities.AppUser;
import com.projecthub.core.repositories.TokenRepository;
import com.projecthub.core.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenManagementService {
    private static final Logger logger = LoggerFactory.getLogger(TokenManagementService.class);
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    public TokenManagementService(JwtUtil jwtUtil, TokenRepository tokenRepository) {
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
    }

    public String generateAccessToken(AppUser user) {
        return jwtUtil.generateToken(user.getUsername());
    }

    @Transactional
    public void revokeUserTokens(String username) {
        logger.info("Revoking all tokens for user: {}", username);
        tokenRepository.revokeAllUserTokens(username);
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public boolean validateTokenForUser(String token, String username) {
        if (!tokenRepository.isTokenValid(token)) {
            logger.debug("Token is revoked or invalid in repository");
            return false;
        }
        return jwtUtil.validateToken(token, username);
    }

    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }

    public void storeToken(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'storeToken'");
    }
}
