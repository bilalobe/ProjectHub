package com.projecthub.repository.custom;

import java.util.List;
import java.util.Optional;

import com.projecthub.dto.UserSummary;
import com.projecthub.model.User;

public interface CustomUserRepository {
    List<User> findAll();
    User save(UserSummary userDTO);
    void deleteById(Long userId);
    Optional<User> findById(Long id);
    List<User> findByTeamId(Long teamId);
}