package com.projecthub.core.repositories;

import com.projecthub.core.entities.RememberMeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface RememberMeTokenRepository extends JpaRepository<RememberMeToken, UUID> {
    Optional<RememberMeToken> findByTokenValue(String tokenValue);

    void deleteByUser_Username(String username);
}