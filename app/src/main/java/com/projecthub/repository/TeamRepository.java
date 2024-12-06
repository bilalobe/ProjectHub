package com.projecthub.repository;

import com.projecthub.model.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Team} entities.
 */
@Repository
public interface TeamRepository {
    Team save(Team team);
    List<Team> findAll();
    Optional<Team> findById(UUID id);
    void delete(Team team);
    Team getReferenceById(UUID teamId);
    void deleteById(UUID teamId);
    List<Team> findByCohortId(UUID cohortId);
    boolean existsById(UUID id);
}