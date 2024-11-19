package com.projecthub.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projecthub.dto.ProjectSummary;
import com.projecthub.model.Project;
import com.projecthub.model.Team;
import com.projecthub.repository.custom.CustomProjectRepository;
import com.projecthub.repository.custom.CustomTeamRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * Service layer for Project operations.
 * Uses ProjectSummary for data transfer.
 */
@Service
@Tag(name = "Project Service", description = "Operations pertaining to projects in ProjectHub")
public class ProjectService {

    private final CustomProjectRepository projectRepository;
    private final CustomTeamRepository teamRepository;

    @Autowired
    public ProjectService(@Qualifier("csvProjectRepository") CustomProjectRepository projectRepository,
                          @Qualifier("csvTeamRepository") CustomTeamRepository teamRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
    }

    @Operation(summary = "View a list of all projects", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    })
    public List<ProjectSummary> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Find a project by ID", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved project"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    @Operation(summary = "Find all projects for a team", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved projects")
    })
    public List<ProjectSummary> getProjectsByTeamId(Long teamId) {
        return projectRepository.findAllByTeamId(teamId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Find a project with its components by ID", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved project with components"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public Optional<ProjectSummary> getProjectWithComponentsById(Long id) {
        Optional<Project> projectOpt = Optional.ofNullable(projectRepository.findProjectWithComponentsById(id));
        return projectOpt.map(this::convertToDTO);
    }

    @Operation(summary = "Save a project", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully saved project"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid project data")
    })
    public ProjectSummary saveProject(ProjectSummary ProjectSummary) {
        Project project = convertToEntity(ProjectSummary);

        if (ProjectSummary.getTeamId() != null) {
            Optional<Team> teamOpt = teamRepository.findById(ProjectSummary.getTeamId());
            if (teamOpt.isPresent()) {
                project.setTeam(teamOpt.get());
            } else {
                throw new RuntimeException("Team not found with ID: " + ProjectSummary.getTeamId());
            }
        }

        Project savedProject = projectRepository.save(project);
        return convertToDTO(savedProject);
    }
    
    @Operation(summary = "Delete a project by ID", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully deleted project"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    /**
     * Converts a Project entity to ProjectSummary.
     *
     * @param project the Project entity
     * @return the ProjectSummary
     */
    private ProjectSummary convertToDTO(Project project) {
        Long teamId = null;
        if (project.getTeam() != null) {
            teamId = project.getTeam().getId();
        }
        return new ProjectSummary(
            project.getId(),
            project.getName(),
            project.getDescription(),
            teamId
        );
    }

    /**
     * Converts a ProjectSummary to Project entity.
     *
     * @param ProjectSummary the ProjectSummary
     * @return the Project entity
     */
    private Project convertToEntity(ProjectSummary ProjectSummary) {
        Project project = new Project();
        project.setId(ProjectSummary.getId());
        project.setName(ProjectSummary.getName());
        project.setDescription(ProjectSummary.getDescription());
        // Team is set in saveProject method
        return project;
    }

    public Project saveProject(Project project) {
        if (project.getTeam() != null) {
            Optional<Team> teamOpt = teamRepository.findById(project.getTeam().getId());
            if (teamOpt.isPresent()) {
                project.setTeam(teamOpt.get());
            } else {
                throw new RuntimeException("Team not found with ID: " + project.getTeam().getId());
            }
        }
        return projectRepository.save(project);
    }       
}