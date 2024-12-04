package com.projecthub.dto;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Data Transfer Object for the Team entity.
 * Used for transferring team data between processes.
 */
public class TeamDTO {

    /**
     * The unique identifier of the team.
     */
    @CsvBindByName
    @NotNull(message = "Team ID cannot be null")
    private UUID id;

    /**
     * The name of the team.
     */
    @CsvBindByName
    @NotBlank(message = "Team name is mandatory")
    private String name;

    /**
     * The unique identifier of the associated school.
     */
    @CsvBindByName
    @NotNull(message = "School ID cannot be null")
    private UUID schoolId;

    /**
     * The unique identifier of the associated cohort.
     */
    @CsvBindByName
    @NotNull(message = "Cohort ID cannot be null")
    private UUID cohortId;

    // No-argument constructor
    public TeamDTO() {}

    /**
     * Constructs a new TeamDTO with the specified details.
     *
     * @param id       the unique identifier of the team
     * @param name     the name of the team
     * @param schoolId the unique identifier of the associated school
     * @param cohortId the unique identifier of the associated cohort
     */
    public TeamDTO(UUID id, String name, UUID schoolId, UUID cohortId) {
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.schoolId = schoolId;
        this.cohortId = cohortId;
    }

    /**
     * Gets the unique identifier of the team.
     *
     * @return the team's unique identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the team.
     *
     * @param id the team's unique identifier
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Gets the name of the team.
     *
     * @return the team's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the team.
     *
     * @param name the team's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the unique identifier of the associated school.
     *
     * @return the school's unique identifier
     */
    public UUID getSchoolId() {
        return schoolId;
    }

    /**
     * Sets the unique identifier of the associated school.
     *
     * @param schoolId the school's unique identifier
     */
    public void setSchoolId(UUID schoolId) {
        this.schoolId = schoolId;
    }

    /**
     * Gets the unique identifier of the associated cohort.
     *
     * @return the cohort's unique identifier
     */
    public UUID getCohortId() {
        return cohortId;
    }

    /**
     * Sets the unique identifier of the associated cohort.
     *
     * @param cohortId the cohort's unique identifier
     */
    public void setCohortId(UUID cohortId) {
        this.cohortId = cohortId;
    }
}