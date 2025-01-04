package com.projecthub.base.auth.infrastructure.persistence.token;

import com.projecthub.base.auth.domain.entity.RememberMeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RememberMeTokenRepository extends JpaRepository<RememberMeToken, UUID> {
    Optional<RememberMeToken> findByTokenValue(String tokenValue);

    void deleteByUser_Username(String username);
}
