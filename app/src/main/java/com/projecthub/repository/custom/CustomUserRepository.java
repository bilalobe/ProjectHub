package com.projecthub.repository.custom;

import java.util.List;
import java.util.Optional;

import com.projecthub.model.User;

public interface CustomUserRepository {
    List<User> findAll();
    User save(User user);
    void deleteById(Long userId);
    Optional<User> findById(Long id);
    List<User> findByTeamId(Long teamId);
    Optional<User> findByUsername(String username);
}