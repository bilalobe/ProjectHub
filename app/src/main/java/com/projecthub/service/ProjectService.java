package com.projecthub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projecthub.model.Project;
import com.projecthub.repository.custom.CustomProjectRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Service
@Api(value = "Project Service", description = "Operations pertaining to projects in ProjectHub")
public class ProjectService {

    private final CustomProjectRepository projectRepository;

    public ProjectService(@Qualifier("csvProjectRepository") CustomProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @ApiOperation(value = "View a list of all projects", response = List.class)
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @ApiOperation(value = "Find a project by ID", response = Project.class)
    public Optional<Project> getProjectById(Long id) {
        return Optional.ofNullable(projectRepository.findProjectWithComponentsById(id));
    }

    @ApiOperation(value = "Find all projects for a team", response = List.class)
    public List<Project> getProjectsByTeamId(Long teamId) {
        return projectRepository.findAllByTeamId(teamId);
    }

    @ApiOperation(value = "Find a project with its components by ID", response = Project.class)
    public Project getProjectWithComponentsById(Long id) {
        return projectRepository.findProjectWithComponentsById(id);
    }

    @ApiOperation(value = "Save a project")
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    @ApiOperation(value = "Delete a project by ID")
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}