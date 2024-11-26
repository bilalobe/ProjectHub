package com.projecthub.dto;

import com.projecthub.model.Project;

import java.time.LocalDate;

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
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String status;

    /**
     * Constructs a ProjectSummary with the specified details.
     *
     * @param id the project ID
     * @param name the project name
     * @param description the project description
     * @param teamId the team ID associated with the project
     * @param deadline the project deadline
     * @param startDate the project start date
     * @param endDate the project end date
     * @param status the project status
     */
    public ProjectSummary(Long id, String name, String description, Long teamId, String deadline, LocalDate startDate, LocalDate endDate, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.teamId = teamId;
        this.deadline = deadline;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
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
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        this.status = project.getStatus();
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

    /**
     * Gets the project start date.
     *
     * @return the project start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Gets the project end date.
     *
     * @return the project end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Gets the project status.
     *
     * @return the project status
     */
    public String getStatus() {
        return status;
    }
}