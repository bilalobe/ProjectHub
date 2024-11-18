package com.projecthub.dto;

import com.projecthub.model.Component;

/**
 * Data Transfer Object for the Component entity.
 * Used for transferring component data between processes.
 */
public class ComponentSummary {
    private Long id;
    private String name;
    private String description;
    private Long projectId;

    public ComponentSummary() {}

    public ComponentSummary(Long id, String name, String description, Long projectId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.projectId = projectId;
    }

    public ComponentSummary(Component component) {
        this.id = component.getId();
        this.name = component.getName();
        this.description = component.getDescription();
        this.projectId = component.getProject() != null ? component.getProject().getId() : null;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getProjectId() {
        return projectId;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}