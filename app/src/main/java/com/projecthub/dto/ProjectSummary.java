package com.projecthub.dto;

import com.projecthub.model.Project;

/**
 * Data Transfer Object for the Project entity.
 * Used for transferring project data between processes.
 */
public class ProjectSummary {
    private final Long id;
    private final String name;
    private final String description;
    private final Long teamId;
    private final String deadline;

    /**
     * Default constructor.
     */
    public ProjectSummary(ProjectSummary project) {
        this.id = 0L;
        this.name = "";
        this.description = "";
        this.teamId = 0L;
        this.deadline = "";
    }

    /**
     * Constructs a ProjectSummary from a Project entity.
     *
     * @param project the Project entity
     */
    public ProjectSummary(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.teamId = project.getTeam() != null ? project.getTeam().getId() : null;
        this.deadline = project.getDeadline();
    }

    /**
     * Gets the project ID.
     *
     * @return the project ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the project deadline.
     *
     * @return the project deadline
     */
    public String getDeadline() {
        return deadline;
    }

    /**
     * Gets the project name.
     *
     * @return the project name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the project description.
     *
     * @return the project description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the team ID associated with the project.
     *
     * @return the team ID
     */
    public Long getTeam() {
        return teamId;
    }
}