package com.projecthub.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.dto.ProjectSummary;
import com.projecthub.dto.TeamSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.TeamMapper;
import com.projecthub.model.AppUser;
import com.projecthub.model.Cohort;
import com.projecthub.model.School;
import com.projecthub.model.Team;
import com.projecthub.repository.jpa.CohortRepository;
import com.projecthub.repository.jpa.SchoolRepository;
import com.projecthub.repository.custom.CustomTeamRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Service
@Tag(name = "Team Service", description = "Operations pertaining to teams in ProjectHub")
public class TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

    private final CustomTeamRepository teamRepository;
    private final UserService userService;

    @Autowired
    private CohortRepository cohortRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private TeamMapper teamMapper;

    public TeamService(CustomTeamRepository teamRepository, UserService userService) {
        this.teamRepository = teamRepository;
        this.userService = userService;
    }

    /**
     * Adds a user to a team.
     *
     * @param teamId the ID of the team
     * @param userId the ID of the user
     * @return the updated TeamSummary
     * @throws ResourceNotFoundException if the team or user is not found
     * @throws IllegalArgumentException if teamId or userId is null
     */
    @Operation(summary = "Add a user to a team")
    @Transactional
    public TeamSummary addUserToTeam(Long teamId, Long userId) throws ResourceNotFoundException {
        logger.info("Adding user {} to team {}", userId, teamId);
    
        if (teamId == null || userId == null) {
            throw new IllegalArgumentException("Team ID and User ID cannot be null");
        }
    
        // Retrieve the team and user entities
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + teamId));
    
        AppUser user = userService.getAppUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    
        // Add user to team
        team.getMembers().add(user);
        teamRepository.save(team);
    
        logger.info("User {} added to team {}", userId, teamId);
        return teamMapper.toTeamSummary(team);
    }

    /**
     * Retrieves all teams.
     *
     * @return list of TeamSummary
     */
    @Operation(summary = "Get all teams")
    public List<TeamSummary> getAllTeams() {
        logger.info("Retrieving all teams");
        return teamRepository.findAll().stream()
                .map(teamMapper::toTeamSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a team by ID.
     *
     * @param id the ID of the team
     * @return TeamSummary
     * @throws ResourceNotFoundException if the team is not found
     */
    @Operation(summary = "Get team by ID")
    public TeamSummary getTeamById(Long id) throws ResourceNotFoundException {
        logger.info("Retrieving team with ID {}", id);

        if (id == null) {
            throw new IllegalArgumentException("Team ID cannot be null");
        }

        return teamRepository.findById(id)
                .map(teamMapper::toTeamSummary)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));
    }

    @Operation(summary = "Create a new team")
    public TeamSummary createTeam(TeamSummary teamSummary) {
        // Map TeamSummary to Team entity
        School school = getSchoolById(teamSummary.getSchoolId());
        Cohort cohort = getCohortById(teamSummary.getCohortId());
        Team teamEntity = teamMapper.toTeam(teamSummary, school, cohort);
        Team savedTeam = teamRepository.save(teamEntity);
        return teamMapper.toTeamSummary(savedTeam);
    }

    @Operation(summary = "Delete a team by ID")
    public void deleteTeam(Long id) throws ResourceNotFoundException {
        if (!teamRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Team not found with ID: " + id);
        }
        teamRepository.deleteById(id);
    }

    public List<TeamSummary> getTeamsByCohortId(Long cohortId) {
        return teamRepository.findByCohortId(cohortId).stream()
                .map(teamMapper::toTeamSummary)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Save a team")
    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    @Operation(summary = "Update a team")
    public TeamSummary updateTeam(TeamSummary teamSummary) {
        // Retrieve the existing team
        var existingTeam = teamRepository.findById(teamSummary.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id " + teamSummary.getId()));

        // Update the team's properties
        existingTeam.setName(teamSummary.getName());
        existingTeam.setCohort(getCohortById(teamSummary.getCohortId()));

        // Save the updated team
        Team updatedTeam = teamRepository.save(existingTeam);

        // Convert the updated team entity back to a TeamSummary and return it
        return teamMapper.toTeamSummary(updatedTeam);
    }

    public Cohort getCohortById(Long cohortId) {
        return cohortRepository.findById(cohortId)
                .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with id " + cohortId));
    }

    public List<ProjectSummary> getProjectsByTeamId(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + teamId));
        return team.getProjects().stream()
                .map(ProjectSummary::new)
                .collect(Collectors.toList());
    }

    public List<AppUserSummary> getMembersByTeamId(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + teamId));
        return team.getMembers().stream()
                .map(teamMapper::toAppUserSummary)
                .collect(Collectors.toList());
    }

    public School getSchoolById(Long schoolId) {
        return schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));
    }
}