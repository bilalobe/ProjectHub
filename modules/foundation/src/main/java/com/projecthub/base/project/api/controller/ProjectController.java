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

    public ProjectController(final ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        ProjectController.logger.info("Retrieving all projects");
        return ResponseEntity.ok(this.projectService.getAllProjects());
    }

    @Override
    public ResponseEntity<ProjectDTO> getById(final UUID id) {
        ProjectController.logger.info("Retrieving project with ID {}", id);
        return ResponseEntity.ok(this.projectService.getProjectById(id));
    }

    @Override
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody final ProjectDTO project) {
        ProjectController.logger.info("Creating new project");
        return ResponseEntity.ok(this.projectService.saveProject(project));
    }

    @Override
    public ResponseEntity<ProjectDTO> updateProject(final UUID id, @Valid @RequestBody final ProjectDTO project) {
        ProjectController.logger.info("Updating project with ID {}", id);
        return ResponseEntity.ok(this.projectService.updateProject(id, project));
    }

    @Override
    public ResponseEntity<Void> deleteById(final UUID id) {
        ProjectController.logger.info("Deleting project with ID {}", id);
        this.projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
