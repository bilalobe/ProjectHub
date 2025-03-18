package com.projecthub.base.team.application.service;


import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.infrastructure.mapper.ProjectMapper;
import com.projecthub.base.project.infrastructure.repository.ProjectJpaRepository;
import com.projecthub.base.repository.jpa.AppUserJpaRepository;
import com.projecthub.base.repository.jpa.TeamJpaRepository;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.student.domain.entity.Student;
import com.projecthub.base.team.api.dto.TeamDTO;
import com.projecthub.base.team.application.mapper.TeamMapper;
import com.projecthub.base.team.domain.entity.Team;
import com.projecthub.base.user.api.dto.AppUserDTO;
import com.projecthub.base.user.api.mapper.AppUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing teams.
 */
@Service
public class TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

    private final TeamJpaRepository teamRepository;
    private final AppUserJpaRepository appUserRepository;
    private final ProjectJpaRepository projectRepository;
    private final TeamMapper teamMapper;
    private final ProjectMapper projectMapper;
    private final AppUserMapper appUserMapper;

    public TeamService(final TeamJpaRepository teamRepository, final AppUserJpaRepository appUserRepository, final ProjectJpaRepository projectRepository, final TeamMapper teamMapper, final ProjectMapper projectMapper, final AppUserMapper appUserMapper) {
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
    public TeamDTO createTeam(final TeamDTO teamDTO) {
        TeamService.logger.info("Creating a new team");
        this.validateTeamDTO(teamDTO);
        final Team team = this.teamMapper.toEntity(teamDTO);
        final Team savedTeam = this.teamRepository.save(team);
        TeamService.logger.info("Team created with ID: {}", savedTeam.getId());
        return this.teamMapper.toDto(savedTeam);
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
    public TeamDTO updateTeam(final UUID id, final TeamDTO teamDTO) {
        TeamService.logger.info("Updating team with ID: {}", id);
        this.validateTeamDTO(teamDTO);
        final Team existingTeam = this.findTeamById(id);
        this.teamMapper.updateEntityFromDto(teamDTO, existingTeam);
        final Team updatedTeam = this.teamRepository.save(existingTeam);
        TeamService.logger.info("Team updated with ID: {}", updatedTeam.getId());
        return this.teamMapper.toDto(updatedTeam);
    }

    /**
     * Deletes a team by ID.
     *
     * @param id the ID of the team to delete
     * @throws ResourceNotFoundException if the team is not found
     */
    @Transactional
    public void deleteTeam(final UUID id) {
        TeamService.logger.info("Deleting team with ID: {}", id);
        if (!this.teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team not found with ID: " + id);
        }
        this.teamRepository.deleteById(id);
        TeamService.logger.info("Team deleted with ID: {}", id);
    }

    /**
     * Retrieves a team by ID.
     *
     * @param id the ID of the team to retrieve
     * @return the team DTO
     * @throws ResourceNotFoundException if the team is not found
     */
    public TeamDTO getTeamById(final UUID id) {
        TeamService.logger.info("Retrieving team with ID: {}", id);
        final Team team = this.findTeamById(id);
        return this.teamMapper.toDto(team);
    }

    /**
     * Retrieves all teams.
     *
     * @return a list of team DTOs
     */
    public List<TeamDTO> getAllTeams() {
        TeamService.logger.info("Retrieving all teams");
        return this.teamRepository.findAll().stream()
            .map(this.teamMapper::toDto)
            .toList();
    }

    private void validateTeamDTO(final TeamDTO teamDTO) {
        if (null == teamDTO) {
            throw new IllegalArgumentException("TeamDTO cannot be null");
        }
        // Additional validation logic can be added here
    }

    private Team findTeamById(final UUID id) {
        return this.teamRepository.findById(id)
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
    public TeamDTO addUserToTeam(final UUID teamId, final UUID userId) {
        TeamService.logger.info("Adding user with ID: {} to team with ID: {}", userId, teamId);
        final Team team = this.findTeamById(teamId);
        this.appUserRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        final Student student = new Student();
        team.getStudents().add(student);
        final Team updatedTeam = this.teamRepository.save(team);
        TeamService.logger.info("User added to team with ID: {}", updatedTeam.getId());
        return this.teamMapper.toDto(updatedTeam);
    }

    /**
     * Retrieves teams by cohort ID.
     *
     * @param cohortId the ID of the cohort
     * @return a list of team DTOs
     */
    public List<TeamDTO> getTeamsByCohortId(final UUID cohortId) {
        TeamService.logger.info("Retrieving teams for cohort ID: {}", cohortId);
        return this.teamRepository.findByCohortId(cohortId).stream()
            .map(this.teamMapper::toDto)
            .toList();
    }

    /**
     * Retrieves projects by team ID.
     *
     * @param teamId the ID of the team
     * @return a list of project DTOs
     */
    public List<ProjectDTO> getProjectsByTeamId(final UUID teamId) {
        TeamService.logger.info("Retrieving projects for team ID: {}", teamId);
        this.findTeamById(teamId);
        return this.projectRepository.findAllByTeamId(teamId).stream()
            .map(this.projectMapper::toDto)
            .toList();
    }

    /**
     * Retrieves members by team ID.
     *
     * @param teamId the ID of the team
     * @return a list of app user DTOs
     */
    public List<AppUserDTO> getMembersByTeamId(final UUID teamId) {
        TeamService.logger.info("Retrieving members for team ID: {}", teamId);
        final Team team = this.findTeamById(teamId);
        return team.getStudents().stream()
            .map(student -> {
                final UUID studentId = Student.getId();
                if (null == studentId) {
                    throw new IllegalArgumentException("Student ID cannot be null");
                }
                return this.appUserRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + studentId));
            })
            .map(this.appUserMapper::toDto)
            .toList();
    }

    /**
     * Retrieves the team name by ID.
     *
     * @param teamId the ID of the team
     * @return the name of the team
     * @throws ResourceNotFoundException if the team is not found
     */
    public String getTeamNameById(final UUID teamId) {
        TeamService.logger.info("Retrieving team name for team ID: {}", teamId);
        final Team team = this.findTeamById(teamId);
        return team.getName();
    }
}
