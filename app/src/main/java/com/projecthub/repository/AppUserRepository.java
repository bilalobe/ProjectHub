package com.projecthub.repository;

import com.projecthub.model.AppUser;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link AppUser} entities.
 */
public interface AppUserRepository {
    AppUser save(AppUser user);
    List<AppUser> findAll();
    Optional<AppUser> findById(UUID id);
    Optional<AppUser> findByUsername(String username);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}