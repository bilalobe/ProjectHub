package com.projecthub.repository;

import com.projecthub.model.AppUser;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AppUserRepository {
    Optional<AppUser> findByUsername(String username);

    public AppUser getReferenceById(Long userId);

    public boolean existsById(Long userId);

    Optional<AppUser> findById(Long assignedUserId);

    public boolean existsByUsername(String username);

    AppUser save(AppUser user);

    public void deleteById(Long id);

    List<AppUser> findAll();
}