package com.projecthub.dto;

import com.projecthub.model.Project;

public class ProjectSummary {
    private Long id;
    private String name;
    private String description;
    private Long teamId;
    private String deadline;

    public ProjectSummary() {}

    public ProjectSummary(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.teamId = project.getTeam().getId();
        this.deadline = project.getDeadline();
    }

    public ProjectSummary(Long id, String name, String description, Long teamId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}