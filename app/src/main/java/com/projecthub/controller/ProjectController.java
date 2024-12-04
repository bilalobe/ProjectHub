package com.projecthub.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projecthub.dto.ProjectDTO;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "Project API", description = "Operations pertaining to projects in ProjectHub")
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(summary = "Get all projects")
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        logger.info("Retrieving all projects");
        List<ProjectDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @Operation(summary = "Get project by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable UUID id) {
        logger.info("Retrieving project with ID {}", id);
        try {
            ProjectDTO project = projectService.getProjectById(id);
            return ResponseEntity.ok(project);
        } catch (ResourceNotFoundException e) {
            logger.error("Project not found with ID {}", id, e);
            return ResponseEntity.status(404).body(null);
        }
    }

    @Operation(summary = "Create a new project")
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectSummary) {
        logger.info("Creating a new project");
        try {
            ProjectDTO savedProject = projectService.createProject(projectSummary);
            return ResponseEntity.ok(savedProject);
        } catch (Exception e) {
            logger.error("Error creating project", e);
            return ResponseEntity.status(400).body(null);
        }
    }

    @Operation(summary = "Update an existing project")
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable UUID id, @Valid @RequestBody ProjectDTO projectSummary) {
        logger.info("Updating project with ID {}", id);
        try {
            ProjectDTO updatedProjectSummary = projectService.updateProject(id, projectSummary);
            return ResponseEntity.ok(updatedProjectSummary);
        } catch (ResourceNotFoundException e) {
            logger.error("Project not found with ID {}", id, e);
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            logger.error("Error updating project", e);
            return ResponseEntity.status(400).body(null);
        }
    }

    @Operation(summary = "Delete a project by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable UUID id) {
        logger.info("Deleting project with ID {}", id);
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok("Project deleted successfully");
        } catch (ResourceNotFoundException e) {
            logger.error("Project not found with ID {}", id, e);
            return ResponseEntity.status(404).body("Project not found with ID " + id);
        }
    }
}