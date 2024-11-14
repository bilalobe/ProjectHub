package com.projecthub.repository.custom;

import java.util.List;

import com.projecthub.model.Team;

public interface CustomTeamRepository {
    List<Team> findAll();
    Team save(Team team);
    void deleteById(Long teamId);
}