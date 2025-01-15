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
    public Token save(final Token token) {
        TokenJpaRepository.log.debug("Saving token: {}", token.getId());
        return this.jpaRepository.save(token);
    }

    @Override
    public Optional<Token> findByToken(final String token) {
        return this.jpaRepository.findByToken(token);
    }

    @Override
    public Optional<Token> findById(final UUID id) {
        return this.jpaRepository.findById(id);
    }

    @Override
    @SneakyThrows // For token validation exceptions
    public boolean isValid(final String token) {
        return this.findByToken(token)
            .map(Token::isValid)
            .orElse(false);
    }

    @Override
    @FieldNameConstants // For JPA queries
    public void deleteExpired() {
        this.jpaRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
