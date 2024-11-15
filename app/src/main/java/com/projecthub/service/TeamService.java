package com.projecthub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projecthub.model.Team;
import com.projecthub.model.User;
import com.projecthub.repository.custom.CustomTeamRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Service
@Tag(name = "Team Service", description = "Operations pertaining to teams in ProjectHub")
public class TeamService {

    private final CustomTeamRepository teamRepository;
    private final UserService userService;

    public TeamService(
            @Qualifier("csvTeamRepository") CustomTeamRepository teamRepository,
            UserService userService) {
        this.teamRepository = teamRepository;
        this.userService = userService;
    }

    @Operation(summary = "Add user to team")
    public Team addTeamMember(Long teamId, Long userId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        if (!teamOpt.isPresent()) {
            throw new RuntimeException("Team not found with id: " + teamId);
        }

        Optional<User> userOpt = userService.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        Team team = teamOpt.get();
        User user = userOpt.get();
        
        user.setTeam(team);
        team.getMembers().add(user);
        return teamRepository.save(team);
    }

    @Operation(summary = "View a list of available teams")
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @Operation(summary = "Save a team")
    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    @Operation(summary = "Delete a team by ID")
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    public List<Team> getTeamsByClassId(Long classId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}