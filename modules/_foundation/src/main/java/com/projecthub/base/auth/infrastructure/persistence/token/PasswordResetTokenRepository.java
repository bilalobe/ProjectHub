package com.projecthub.base.auth.infrastructure.persistence.token;

import com.projecthub.base.auth.domain.entity.PasswordResetToken;
import com.projecthub.base.shared.repository.common.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends BaseRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByToken(String token);
}
