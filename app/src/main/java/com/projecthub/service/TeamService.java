package com.projecthub.service;

import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.TeamMapper;
import com.projecthub.dto.TeamSummary;
import com.projecthub.model.AppUser;
import com.projecthub.model.Cohort;
import com.projecthub.model.School;
import com.projecthub.model.Team;
import com.projecthub.repository.AppUserRepository;
import com.projecthub.repository.CohortRepository;
import com.projecthub.repository.SchoolRepository;
import com.projecthub.repository.TeamRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing teams.
 * Handles creation, updating, deletion, and retrieval of teams.
 */
 
/**
 * Creates a new team based on the provided team summary.
 *
 * @param teamSummary the summary information of the team to be created
 * @return the summary information of the created team
 * @throws IllegalArgumentException if the team summary is invalid
 */
 
/**
 * Updates an existing team with the provided team summary.
 *
 * @param teamId the ID of the team to update
 * @param teamSummary the summary information to update the team with
 * @return the updated team summary
 * @throws IllegalArgumentException if the team summary is invalid
 */
 
/**
 * Deletes the team with the specified ID.
 *
 * @param teamId the ID of the team to delete
 */
 
/**
 * Retrieves the team with the specified ID.
 *
 * @param teamId the ID of the team to retrieve
 * @return the summary information of the retrieved team
 * @throws ResourceNotFoundException if the team with the specified ID is not found
 */
 
/**
 * Retrieves all teams.
 *
 * @return a list of summary information for all teams
 */
 
/**
 * Adds a user to the specified team.
 *
 * @param teamId the ID of the team to add the user to
 * @param userId the ID of the user to add
 * @return the updated team summary
 */
 
/**
 * Removes a user from the specified team.
 *
 * @param teamId the ID of the team to remove the user from
 * @param userId the ID of the user to remove
 * @return the updated team summary
 * @throws IllegalArgumentException if the user is not a member of the team
 */
@Service
public class TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;
    private final CohortRepository cohortRepository;
    private final SchoolRepository schoolRepository;
    private final AppUserRepository appUserRepository;
    private final TeamMapper teamMapper;

    public TeamService(
            TeamRepository teamRepository,
            CohortRepository cohortRepository,
            SchoolRepository schoolRepository,
            AppUserRepository appUserRepository,
            TeamMapper teamMapper) {

        this.teamRepository = teamRepository;
        this.cohortRepository = cohortRepository;
        this.schoolRepository = schoolRepository;
        this.appUserRepository = appUserRepository;
        this.teamMapper = teamMapper;
    }

    @Transactional
    public TeamSummary createTeam(TeamSummary teamSummary) {
        logger.info("Creating a new team");
        validateTeamSummary(teamSummary);

        School school = schoolRepository.getReferenceById(teamSummary.getSchoolId());
        Cohort cohort = cohortRepository.getReferenceById(teamSummary.getCohortId());

        Team team = teamMapper.toTeam(teamSummary, school, cohort);
        Team savedTeam = teamRepository.save(team);

        logger.info("Team created with ID {}", savedTeam.getId());
        return teamMapper.toTeamSummary(savedTeam);
    }

    @Transactional
    public TeamSummary updateTeam(Long teamId, TeamSummary teamSummary) {
        logger.info("Updating team with ID {}", teamId);
        validateTeamSummary(teamSummary);

        Team team = teamRepository.getReferenceById(teamId);
        teamMapper.updateTeamFromSummary(teamSummary, team);

        Team updatedTeam = teamRepository.save(team);
        logger.info("Team updated with ID {}", updatedTeam.getId());
        return teamMapper.toTeamSummary(updatedTeam);
    }

    @Transactional
    public void deleteTeam(Long teamId) {
        logger.info("Deleting team with ID {}", teamId);
        teamRepository.deleteById(teamId);
        logger.info("Team deleted with ID {}", teamId);
    }

    public TeamSummary getTeamById(Long teamId) {
        logger.info("Retrieving team with ID {}", teamId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + teamId));
        return teamMapper.toTeamSummary(team);
    }

    public List<TeamSummary> getAllTeams() {
        logger.info("Retrieving all teams");
        List<Team> teams = teamRepository.findAll();
        return teams.stream()
                .map(teamMapper::toTeamSummary)
                .toList();
    }

    @Transactional
    public TeamSummary addUserToTeam(Long teamId, Long userId) {
        logger.info("Adding user {} to team {}", userId, teamId);

        Team team = teamRepository.getReferenceById(teamId);
        AppUser user = appUserRepository.getReferenceById(userId);

        team.getMembers().add(user);
        Team updatedTeam = teamRepository.save(team);

        logger.info("User {} added to team {}", userId, teamId);
        return teamMapper.toTeamSummary(updatedTeam);
    }

    @Transactional
    public TeamSummary removeUserFromTeam(Long teamId, Long userId) {
        logger.info("Removing user {} from team {}", userId, teamId);

        Team team = teamRepository.getReferenceById(teamId);
        AppUser user = appUserRepository.getReferenceById(userId);

        if (!team.getMembers().remove(user)) {
            throw new IllegalArgumentException("User is not a member of the team");
        }

        Team updatedTeam = teamRepository.save(team);
        logger.info("User {} removed from team {}", userId, teamId);
        return teamMapper.toTeamSummary(updatedTeam);
    }

    private void validateTeamSummary(TeamSummary teamSummary) {
        if (teamSummary == null) {
            throw new IllegalArgumentException("TeamSummary cannot be null");
        }
        if (teamSummary.getName() == null || teamSummary.getName().isEmpty()) {
            throw new IllegalArgumentException("Team name cannot be null or empty");
        }
        if (teamSummary.getCohortId() == null) {
            throw new IllegalArgumentException("Cohort ID cannot be null");
        }
        if (teamSummary.getSchoolId() == null) {
            throw new IllegalArgumentException("School ID cannot be null");
        }
    }

    public TeamSummary[] getTeamsByCohortId(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
