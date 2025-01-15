package com.projecthub.base.auth.infrastructure.persistence.token;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public class TokenRepository {
    private final EntityManager entityManager;

    public TokenRepository(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void revokeAllUserTokens(final String username) {
        this.entityManager.createQuery(
                "UPDATE Token t SET t.revoked = true WHERE t.user.username = :username")
            .setParameter("username", username)
            .executeUpdate();
    }

    public boolean isTokenValid(final String token) {
        final Long count = this.entityManager.createQuery(
                "SELECT COUNT(t) FROM Token t WHERE t.tokenValue = :token AND t.revoked = false AND t.expiryDate > :now",
                Long.class)
            .setParameter("token", token)
            .setParameter("now", LocalDateTime.now())
            .getSingleResult();
        return 0 < count;
    }

    @Transactional
    public void revokeToken(final String token) {
        this.entityManager.createQuery(
                "UPDATE Token t SET t.revoked = true WHERE t.tokenValue = :token")
            .setParameter("token", token)
            .executeUpdate();
    }
}
