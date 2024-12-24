package com.projecthub.core.repositories.jpa;

import com.projecthub.core.entities.AppUser;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link AppUser} entities.
 */
@Repository("jpaUserRepository")
@Profile("jpa")
@Primary

public interface AppUserJpaRepository extends JpaRepository<AppUser, UUID> {

    /**
     * Finds a user by username.
     *
     * @param username the username of the user
     * @return an {@code Optional} containing the user if found
     */
    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<AppUser> findByResetToken(String token);

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByVerificationToken(String token);

    @Modifying
    @Query("UPDATE AppUser u SET u.verificationToken = NULL, u.verificationTokenExpiry = NULL " +
            "WHERE u.verificationTokenExpiry < ?1 AND u.verificationToken IS NOT NULL")
    int deleteExpiredVerificationTokens(LocalDateTime now);
}