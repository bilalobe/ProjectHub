package com.projecthub.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projecthub.dto.ProjectSummary;
import com.projecthub.model.Project;
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
    public String createProject(@Valid @RequestBody Project project) {
        projectService.saveProject(project);
        return "Project created successfully";
    }

    @DeleteMapping("/{id}")
    public String deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return "Project deleted successfully";
    }
}