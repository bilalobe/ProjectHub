package com.projecthub.repository.custom;

import java.util.List;
import java.util.Optional;

import com.projecthub.model.Team;

public interface CustomTeamRepository {
    List<Team> findAll();
    Team save(Team team);
    void deleteById(Long teamId);
    Optional<Team> findById(Long id);
}