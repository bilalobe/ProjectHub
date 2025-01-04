package com.projecthub.base.project.api.controller;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.api.rest.ProjectApi;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController implements ProjectApi {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        logger.info("Retrieving all projects");
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @Override
    public ResponseEntity<ProjectDTO> getById(UUID id) {
        logger.info("Retrieving project with ID {}", id);
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @Override
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO project) {
        logger.info("Creating new project");
        return ResponseEntity.ok(projectService.saveProject(project));
    }

    @Override
    public ResponseEntity<ProjectDTO> updateProject(UUID id, @Valid @RequestBody ProjectDTO project) {
        logger.info("Updating project with ID {}", id);
        return ResponseEntity.ok(projectService.updateProject(id, project));
    }

    @Override
    public ResponseEntity<Void> deleteById(UUID id) {
        logger.info("Deleting project with ID {}", id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
