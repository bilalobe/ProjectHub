package com.projecthub.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.projecthub.dto.ProjectSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.ProjectMapper;
import com.projecthub.model.Project;
import com.projecthub.model.Team;
import com.projecthub.repository.custom.CustomProjectRepository;
import com.projecthub.repository.custom.CustomTeamRepository;

/**
 * Service class for managing projects within the ProjectHub application.
 * It provides methods to create, retrieve, update, and delete projects.
 */
@Service
public class ProjectService {

    private final CustomProjectRepository projectRepository;
    private final CustomTeamRepository teamRepository;
    private final ProjectMapper projectMapper;

    public ProjectService(CustomProjectRepository projectRepository, CustomTeamRepository teamRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.projectMapper = projectMapper;
    }

    /**
     * Saves a new project based on the provided {@link ProjectSummary} DTO.
     * 
     * @param projectSummary the summary of the project to be created
     * @return the saved {@link ProjectSummary} entity
     * @throws ResourceNotFoundException if the associated team is not found
     */
    public ProjectSummary saveProject(ProjectSummary projectSummary) throws ResourceNotFoundException {
        Team team = teamRepository.findById(projectSummary.getTeam())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + projectSummary.getTeam()));

        Project project = projectMapper.toProject(projectSummary, team);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toProjectSummary(savedProject);
    }

    /**
     * Updates an existing project based on the provided {@link ProjectSummary} DTO.
     * 
     * @param id the unique identifier of the project to be updated
     * @param projectSummary the summary of the project to be updated
     * @return the updated {@link ProjectSummary} entity
     * @throws ResourceNotFoundException if the project or associated team is not found
     */
    public ProjectSummary updateProject(Long id, ProjectSummary projectSummary) throws ResourceNotFoundException {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));

        Team team = teamRepository.findById(projectSummary.getTeam())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + projectSummary.getTeam()));

        existingProject.setName(projectSummary.getName());
        existingProject.setDescription(projectSummary.getDescription());
        existingProject.setDeadline(projectSummary.getDeadline());
        existingProject.setStartDate(projectSummary.getStartDate());
        existingProject.setEndDate(projectSummary.getEndDate());
        existingProject.setStatus(projectSummary.getStatus());
        existingProject.setTeam(team);

        Project updatedProject = projectRepository.save(existingProject);
        return projectMapper.toProjectSummary(updatedProject);
    }

    /**
     * Retrieves all projects from the repository.
     * 
     * @return a list of all {@link ProjectSummary} objects
     */
    public List<ProjectSummary> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toProjectSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a project by its unique identifier.
     * 
     * @param id the unique identifier of the project
     * @return an {@link Optional} containing the {@link ProjectSummary} if found, else empty
     */
    public Optional<ProjectSummary> getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(projectMapper::toProjectSummary);
    }

    /**
     * Retrieves all projects associated with a team.
     * 
     * @param teamId the unique identifier of the team
     * @return a list of all {@link ProjectSummary} objects associated with the team
     */
    public List<ProjectSummary> getProjectsByTeamId(Long teamId) {
        List<Project> projects = projectRepository.findAllByTeamId(teamId);
        return projects.stream()
                .map(projectMapper::toProjectSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all projects associated with a user.
     * 
     * @param userId the unique identifier of the user
     * @return a list of all {@link ProjectSummary} objects associated with the user
     */
    public List<ProjectSummary> getProjectsByUserId(Long userId) {
        List<Project> projects = projectRepository.findAllByUserId(userId);
        return projects.stream()
                .map(projectMapper::toProjectSummary)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a project based on its unique identifier.
     * 
     * @param id the unique identifier of the project to be deleted
     * @throws ResourceNotFoundException if the project is not found
     */
    public void deleteProject(Long id) throws ResourceNotFoundException {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with ID: " + id);
        }
        projectRepository.deleteById(id);
    }
}