package com.projecthub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projecthub.dto.ProjectSummary;
import com.projecthub.service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Project API", description = "Operations pertaining to projects in ProjectHub")
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Retrieves all projects.
     *
     * @return a list of all ProjectSummary objects
     */
    @Operation(summary = "Get all projects")
    @GetMapping
    public ResponseEntity<List<ProjectSummary>> getAllProjects() {
        List<ProjectSummary> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Creates a new project.
     *
     * @param projectSummary the ProjectSummary object representing the new project
     * @return a response entity with a success message
     */
    @Operation(summary = "Create a new project")
    @PostMapping
    public ResponseEntity<ProjectSummary> createProject(@Valid @RequestBody ProjectSummary projectSummary) {
        ProjectSummary savedProject = projectService.saveProject(projectSummary);
        return ResponseEntity.ok(savedProject);
    }

    /**
     * Updates an existing project.
     *
     * @param id the ID of the project to update
     * @param projectSummary the ProjectSummary object with updated details
     * @return a response entity with the updated ProjectSummary
     */
    @Operation(summary = "Update an existing project")
    @PutMapping("/{id}")
    public ResponseEntity<ProjectSummary> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectSummary projectSummary) {
        ProjectSummary updatedProjectSummary = projectService.updateProject(id, projectSummary);
        return ResponseEntity.ok(updatedProjectSummary);
    }

    /**
     * Deletes a project by ID.
     *
     * @param id the ID of the project to delete
     * @return a response entity with a success message
     */
    @Operation(summary = "Delete a project by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok("Project deleted successfully");
    }
}