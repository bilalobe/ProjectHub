package com.projecthub.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public ProjectService(CustomProjectRepository projectRepository, CustomTeamRepository teamRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
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

        Project project = ProjectMapper.toProject(projectSummary, team);
        Project savedProject = projectRepository.save(project);
        return ProjectMapper.toProjectSummary(savedProject);
    }

    /**
     * Retrieves all projects from the repository.
     * 
     * @return a list of all {@link ProjectSummary} objects
     */
    public List<ProjectSummary> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectMapper::toProjectSummary)
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
                .map(ProjectMapper::toProjectSummary);
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