package com.projecthub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.projecthub.model.Team;
import com.projecthub.model.AppUser;
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

    /**
     * Adds a user to a team.
     *
     * @param teamId the ID of the team
     * @param userId the ID of the user
     * @return the updated team
     */
    @Operation(summary = "Add user to team")
    public Team addTeamMember(Long teamId, Long userId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        if (!teamOpt.isPresent()) {
            throw new RuntimeException("Team not found with id: " + teamId);
        }

        Optional<AppUser> userOpt = userService.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        Team team = teamOpt.get();
        AppUser user = userOpt.get();

        user.setTeam(team);
        team.getMembers().add(user);
        userService.saveUser(user);
        return teamRepository.save(team);
    }

    /**
     * Retrieves a list of all teams.
     *
     * @return a list of all teams
     */
    @Operation(summary = "View a list of available teams")
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    /**
     * Saves a team.
     *
     * @param team the team to save
     * @return the saved team
     */
    @Operation(summary = "Save a team")
    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    /**
     * Deletes a team by ID.
     *
     * @param id the ID of the team to delete
     */
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

    /**
     * Retrieves teams by cohort ID.
     *
     * @param cohortId the ID of the cohort
     * @return a list of teams in the cohort
     */
    @Operation(summary = "Get teams by cohort ID")
    public List<Team> getTeamsByCohortId(Long cohortId) {
        return teamRepository.findByCohortId(cohortId);
    }

    /**
     * Retrieves a team by ID.
     *
     * @param id the ID of the team
     * @return an Optional containing the team if found, or empty if not found
     */
    @Operation(summary = "Retrieve a team by ID")
    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }
}