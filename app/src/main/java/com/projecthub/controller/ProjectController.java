package com.projecthub.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projecthub.dto.ProjectSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.service.ProjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public List<ProjectSummary> getAllProjects() {
        return projectService.getAllProjects().stream()
                .map(ProjectSummary::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<String> createProject(@Valid @RequestBody ProjectSummary projectSummary) {
        try {
            projectService.saveProject(projectSummary);
            return ResponseEntity.ok("Project created successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok("Project deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }
}