package com.projecthub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projecthub.model.Team;
import com.projecthub.repository.custom.CustomTeamRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Service
@Api(value = "Team Service", description = "Operations pertaining to teams in ProjectHub")

public class TeamService {

    private final CustomTeamRepository teamRepository;

    public TeamService(@Qualifier("csvTeamRepository") CustomTeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @ApiOperation(value = "View a list of available teams", response = List.class)
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @ApiOperation(value = "Save a team")
    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    @ApiOperation(value = "Delete a team by ID")
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }
}