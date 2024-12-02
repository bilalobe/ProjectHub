package com.projecthub.service;

import com.projecthub.dto.ProjectSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.ProjectMapper;
import com.projecthub.model.Project;
import com.projecthub.repository.ProjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Tag(name = "Project Service", description = "Operations pertaining to projects in ProjectHub")
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectService(
            ProjectRepository projectRepository,
            ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    @Operation(summary = "Create a new project")
    public ProjectSummary createProject(ProjectSummary projectSummary) {
        Project project = projectMapper.toProject(projectSummary);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toProjectSummary(savedProject);
    }

    @Operation(summary = "Update an existing project")
    public ProjectSummary updateProject(Long id, ProjectSummary projectSummary) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
        projectMapper.updateProjectFromSummary(projectSummary, existingProject);
        Project updatedProject = projectRepository.save(existingProject);
        return projectMapper.toProjectSummary(updatedProject);
    }

    @Operation(summary = "Retrieve a project by ID")
    public Optional<ProjectSummary> getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(projectMapper::toProjectSummary);
    }

    @Operation(summary = "Retrieve all projects")
    public List<ProjectSummary> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toProjectSummary)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Delete a project by ID")
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
        projectRepository.delete(project);
    }
}