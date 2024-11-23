package com.projecthub.repository.custom;

import java.util.List;
import java.util.Optional;

import com.projecthub.model.AppUser;

public interface CustomUserRepository {
    List<AppUser> findAll();
    AppUser save(AppUser user);
    void deleteById(Long userId);
    Optional<AppUser> findById(Long id);
    List<AppUser> findByTeamId(Long teamId);
    Optional<AppUser> findByUsername(String username);

    public boolean existsById(Long userId);
}