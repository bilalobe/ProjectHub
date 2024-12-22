package com.projecthub.core.repositories;

import com.projecthub.core.entities.RememberMeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RememberMeTokenRepository extends JpaRepository<RememberMeToken, String> {
    Optional<RememberMeToken> findByTokenValue(String tokenValue);

    void deleteByUser_Username(String username);
}