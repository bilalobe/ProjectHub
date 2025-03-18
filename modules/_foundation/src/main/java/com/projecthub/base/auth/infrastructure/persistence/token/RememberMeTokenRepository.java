package com.projecthub.base.auth.infrastructure.persistence.token;

import com.projecthub.base.auth.domain.entity.RememberMeToken;
import com.projecthub.base.shared.repository.common.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RememberMeTokenRepository extends BaseRepository<RememberMeToken, UUID> {
    Optional<RememberMeToken> findByTokenValue(String tokenValue);

    void deleteByUser_Username(String username);
}
