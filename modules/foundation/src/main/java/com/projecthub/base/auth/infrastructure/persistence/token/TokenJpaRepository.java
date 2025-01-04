package com.projecthub.base.auth.infrastructure.persistence;

import com.projecthub.base.auth.domain.token.Token;
import com.projecthub.base.auth.domain.token.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository("tokenJpaRepository")
@RequiredArgsConstructor
public class TokenJpaRepository implements TokenRepository {
    private final JpaTokenRepository jpaRepository;

    @Override
    @Synchronized // For concurrent token operations
    public Token save(Token token) {
        log.debug("Saving token: {}", token.getId());
        return jpaRepository.save(token);
    }

    @Override
    public Optional<Token> findByToken(String token) {
        return jpaRepository.findByToken(token);
    }

    @Override
    public Optional<Token> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    @SneakyThrows // For token validation exceptions
    public boolean isValid(String token) {
        return findByToken(token)
            .map(Token::isValid)
            .orElse(false);
    }

    @Override
    @FieldNameConstants // For JPA queries
    public void deleteExpired() {
        jpaRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
