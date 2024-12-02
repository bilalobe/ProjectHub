package com.projecthub.repository;

import com.projecthub.model.Team;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Team} entities.
 */
public interface TeamRepository {
    Team save(Team team);
    List<Team> findAll();
    Optional<Team> findById(Long id);
    void delete(Team team);
    public Team getReferenceById(Long teamId);
    public void deleteById(Long teamId);
}