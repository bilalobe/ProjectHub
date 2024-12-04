package com.projecthub.repository;

import com.projecthub.model.AppUser;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppUserRepository {
    Optional<AppUser> findByUsername(String username);
    AppUser getReferenceById(UUID userId);
    boolean existsById(UUID userId);
    Optional<AppUser> findById(UUID assignedUserId);
    boolean existsByUsername(String username);
    AppUser save(AppUser user);
    void deleteById(UUID id);
    List<AppUser> findAll();
}