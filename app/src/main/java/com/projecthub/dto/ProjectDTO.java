package com.projecthub.dto;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for the Project entity.
 * Used for transferring project data between processes.
 */
public class ProjectDTO {

    /**
     * The unique identifier of the project.
     */
    @CsvBindByName(column = "id")
    @NotNull(message = "Project ID cannot be null")
    private UUID id;

    /**
     * The name of the project.
     */
    @CsvBindByName(column = "name")
    @NotBlank(message = "Project name is mandatory")
    private String name;

    @CsvBindByName(column = "description")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @CsvBindByName(column = "teamId")
    @NotNull(message = "Team ID cannot be null")
    private UUID teamId;

    @CsvBindByName(column = "deadline")
    private LocalDate deadline;

    @CsvBindByName(column = "startDate")
    private LocalDate startDate;

    @CsvBindByName(column = "endDate")
    private LocalDate endDate;

    @CsvBindByName(column = "status")
    private String status;

    // Default constructor
    public ProjectDTO() {}

    /**
     * Constructs a new ProjectDTO with the specified details.
     *
     * @param id          the unique identifier of the project
     * @param name        the name of the project
     * @param description the project description
     * @param teamId      the team ID associated with the project
     * @param deadline    the project deadline
     * @param startDate   the project start date
     * @param endDate     the project end date
     * @param status      the project status
     */
    public ProjectDTO(UUID id, String name, String description, UUID teamId, LocalDate deadline, LocalDate startDate, LocalDate endDate, String status) {
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
     * Gets the unique identifier of the project.
     *
     * @return the project's unique identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the project.
     *
     * @param id the project's unique identifier
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Gets the name of the project.
     *
     * @return the project's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the project.
     *
     * @param name the project's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the project.
     *
     * @return the project's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the project.
     *
     * @param description the project's description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the team ID associated with the project.
     *
     * @return the team's ID
     */
    public UUID getTeamId() {
        return teamId;
    }

    /**
     * Sets the team ID associated with the project.
     *
     * @param teamId the team's ID
     */
    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    /**
     * Gets the deadline of the project.
     *
     * @return the project's deadline
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Sets the deadline of the project.
     *
     * @param deadline the project's deadline
     */
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    /**
     * Gets the start date of the project.
     *
     * @return the project's start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of the project.
     *
     * @param startDate the project's start date
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date of the project.
     *
     * @return the project's end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date of the project.
     *
     * @param endDate the project's end date
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the status of the project.
     *
     * @return the project's status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the project.
     *
     * @param status the project's status
     */
    public void setStatus(String status) {
        this.status = status;
    }
}