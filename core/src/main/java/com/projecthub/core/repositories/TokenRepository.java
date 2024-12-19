package com.projecthub.core.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

@Repository
public class TokenRepository {
    private final EntityManager entityManager;

    public TokenRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void revokeAllUserTokens(String username) {
        entityManager.createQuery(
                        "UPDATE Token t SET t.revoked = true WHERE t.user.username = :username")
                .setParameter("username", username)
                .executeUpdate();
    }

    public boolean isTokenValid(String token) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(t) FROM Token t WHERE t.tokenValue = :token AND t.revoked = false AND t.expiryDate > :now",
                        Long.class)
                .setParameter("token", token)
                .setParameter("now", LocalDateTime.now())
                .getSingleResult();
        return count > 0;
    }

    @Transactional
    public void revokeToken(String token) {
        entityManager.createQuery(
                        "UPDATE Token t SET t.revoked = true WHERE t.tokenValue = :token")
                .setParameter("token", token)
                .executeUpdate();
    }
}
