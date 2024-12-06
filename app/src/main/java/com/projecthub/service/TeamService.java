package com.projecthub.service;

import com.projecthub.dto.AppUserDTO;
import com.projecthub.dto.ProjectDTO;
import com.projecthub.dto.TeamDTO;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.ProjectMapper;
import com.projecthub.mapper.TeamMapper;
import com.projecthub.mapper.AppUserMapper;
import com.projecthub.model.Team;
import com.projecthub.model.Student;
import com.projecthub.model.AppUser;
import com.projecthub.repository.TeamRepository;
import com.projecthub.repository.AppUserRepository;
import com.projecthub.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing teams.
 */
@Service
public class TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;
    private final AppUserRepository appUserRepository;
    private final ProjectRepository projectRepository;
    private final TeamMapper teamMapper;
    private final ProjectMapper projectMapper;
    private final AppUserMapper appUserMapper;

    public TeamService(TeamRepository teamRepository, AppUserRepository appUserRepository, ProjectRepository projectRepository, TeamMapper teamMapper, ProjectMapper projectMapper, AppUserMapper appUserMapper) {
        this.teamRepository = teamRepository;
        this.appUserRepository = appUserRepository;
        this.projectRepository = projectRepository;
        this.teamMapper = teamMapper;
        this.projectMapper = projectMapper;
        this.appUserMapper = appUserMapper;
    }

    /**
     * Creates a new team.
     *
     * @param teamDTO the team data transfer object
     * @return the saved team DTO
     * @throws IllegalArgumentException if teamDTO is null
     */
    @Transactional
    public TeamDTO createTeam(TeamDTO teamDTO) {
        logger.info("Creating a new team");
        validateTeamDTO(teamDTO);
        Team team = teamMapper.toTeam(teamDTO);
        Team savedTeam = teamRepository.save(team);
        logger.info("Team created with ID: {}", savedTeam.getId());
        return teamMapper.toTeamDTO(savedTeam);
    }

    /**
     * Updates an existing team.
     *
     * @param id      the ID of the team to update
     * @param teamDTO the team data transfer object
     * @return the updated team DTO
     * @throws ResourceNotFoundException if the team is not found
     * @throws IllegalArgumentException  if teamDTO is null
     */
    @Transactional
    public TeamDTO updateTeam(UUID id, TeamDTO teamDTO) {
        logger.info("Updating team with ID: {}", id);
        validateTeamDTO(teamDTO);
        Team existingTeam = findTeamById(id);
        teamMapper.updateTeamFromDTO(teamDTO, existingTeam);
        Team updatedTeam = teamRepository.save(existingTeam);
        logger.info("Team updated with ID: {}", updatedTeam.getId());
        return teamMapper.toTeamDTO(updatedTeam);
    }

    /**
     * Deletes a team by ID.
     *
     * @param id the ID of the team to delete
     * @throws ResourceNotFoundException if the team is not found
     */
    @Transactional
    public void deleteTeam(UUID id) {
        logger.info("Deleting team with ID: {}", id);
        if (!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team not found with ID: " + id);
        }
        teamRepository.deleteById(id);
        logger.info("Team deleted with ID: {}", id);
    }

    /**
     * Retrieves a team by ID.
     *
     * @param id the ID of the team to retrieve
     * @return the team DTO
     * @throws ResourceNotFoundException if the team is not found
     */
    public TeamDTO getTeamById(UUID id) {
        logger.info("Retrieving team with ID: {}", id);
        Team team = findTeamById(id);
        return teamMapper.toTeamDTO(team);
    }

    /**
     * Retrieves all teams.
     *
     * @return a list of team DTOs
     */
    public List<TeamDTO> getAllTeams() {
        logger.info("Retrieving all teams");
        return teamRepository.findAll().stream()
                .map(teamMapper::toTeamDTO)
                .collect(Collectors.toList());
    }

    private void validateTeamDTO(TeamDTO teamDTO) {
        if (teamDTO == null) {
            throw new IllegalArgumentException("TeamDTO cannot be null");
        }
        // Additional validation logic can be added here
    }

    private Team findTeamById(UUID id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));
    }

    /**
     * Adds a user to a team.
     *
     * @param teamId the ID of the team
     * @param userId the ID of the user to add
     * @return the updated team DTO
     * @throws ResourceNotFoundException if the team or user is not found
     */
    @Transactional
    public TeamDTO addUserToTeam(UUID teamId, UUID userId) {
        logger.info("Adding user with ID: {} to team with ID: {}", userId, teamId);
        Team team = findTeamById(teamId);
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        Student student = new Student(user.getId(), user.getFirstName(), user.getEmail(), user.getLastName(), team);
        team.getStudents().add(student);
        Team updatedTeam = teamRepository.save(team);
        logger.info("User added to team with ID: {}", updatedTeam.getId());
        return teamMapper.toTeamDTO(updatedTeam);
    }

    /**
     * Retrieves teams by cohort ID.
     *
     * @param cohortId the ID of the cohort
     * @return a list of team DTOs
     */
    public List<TeamDTO> getTeamsByCohortId(UUID cohortId) {
        logger.info("Retrieving teams for cohort ID: {}", cohortId);
        return teamRepository.findByCohortId(cohortId).stream()
                .map(teamMapper::toTeamDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves projects by team ID.
     *
     * @param teamId the ID of the team
     * @return a list of project DTOs
     */
    public List<ProjectDTO> getProjectsByTeamId(UUID teamId) {
        logger.info("Retrieving projects for team ID: {}", teamId);
        findTeamById(teamId);
        return projectRepository.findByTeamId(teamId).stream()
                .map(projectMapper::toProjectDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves members by team ID.
     *
     * @param teamId the ID of the team
     * @return a list of app user DTOs
     */
    public List<AppUserDTO> getMembersByTeamId(UUID teamId) {
        logger.info("Retrieving members for team ID: {}", teamId);
        Team team = findTeamById(teamId);
        return team.getStudents().stream()
                .map(student -> appUserRepository.findById(student.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + student.getId())))
                .map(appUserMapper::toAppUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the team name by ID.
     *
     * @param teamId the ID of the team
     * @return the name of the team
     * @throws ResourceNotFoundException if the team is not found
     */
    public String getTeamNameById(UUID teamId) {
        logger.info("Retrieving team name for team ID: {}", teamId);
        Team team = findTeamById(teamId);
        return team.getName();
    }
}