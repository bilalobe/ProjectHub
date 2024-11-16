package com.projecthub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.projecthub.model.Team;
import com.projecthub.model.User;
import com.projecthub.repository.jpa.TeamRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Service
@Tag(name = "Team Service", description = "Operations pertaining to teams in ProjectHub")
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserService userService;

    @Autowired
    public TeamService(
            TeamRepository teamRepository,
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
        userService.saveUser(user);
        return teamRepository.save(team);    }

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

    /**
     * Retrieves the name of the team based on the provided team ID.
     *
     * @param teamId the ID of the team
     * @return the name of the team, or "N/A" if not found
     */
    public String getTeamNameById(Long teamId) {
        return teamRepository.findById(teamId)
                .map(Team::getName)
                .orElse("N/A");
    }

    @Operation(summary = "Get teams by class ID")
    public List<Team> getTeamsByClassId(Long classId) {
        return teamRepository.findByClassId(classId);
    }
}