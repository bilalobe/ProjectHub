package com.projecthub.dto;

import com.projecthub.model.Team;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for the Team entity.
 * Used for transferring team data between processes.
 */
public class TeamSummary {
    private final Long id;

    @NotBlank(message = "Team name is mandatory")
    private final String name;

    private final Long schoolId;
    private final Long cohortId;
    private final String schoolName;
    private final String cohortName;

    /**
     * Default constructor.
     */
    public TeamSummary() {
        this.id = null;
        this.name = null;
        this.schoolId = null;
        this.cohortId = null;
        this.schoolName = null;
        this.cohortName = null;
    }

    /**
     * Constructs a TeamSummary with specified values.
     *
     * @param id       the team ID
     * @param name     the team's name
     * @param schoolId the school's ID
     * @param cohortId the cohort's ID
     */
    public TeamSummary(Long id, String name, Long schoolId, Long cohortId) {
        this.id = id;
        this.name = name;
        this.schoolId = schoolId;
        this.cohortId = cohortId;
        this.schoolName = null;
        this.cohortName = null;
    }

    /**
     * Constructs a TeamSummary with additional details.
     *
     * @param id         the team ID
     * @param name       the team's name
     * @param schoolId   the school's ID
     * @param cohortId   the cohort's ID
     * @param schoolName the school's name
     * @param cohortName the cohort's name
     */
    public TeamSummary(Long id, String name, Long schoolId, Long cohortId, String schoolName, String cohortName) {
        this.id = id;
        this.name = name;
        this.schoolId = schoolId;
        this.cohortId = cohortId;
        this.schoolName = schoolName;
        this.cohortName = cohortName;
    }

    /**
     * Constructs a TeamSummary from a Team entity.
     *
     * @param team the Team entity
     */
    public TeamSummary(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.schoolId = team.getSchool() != null ? team.getSchool().getId() : null;
        this.cohortId = team.getCohort() != null ? team.getCohort().getId() : null;
        this.schoolName = team.getSchool() != null ? team.getSchool().getName() : null;
        this.cohortName = team.getCohort() != null ? team.getCohort().getName() : null;
    }

    /**
     * Gets the team's ID.
     *
     * @return the team's ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the team's name.
     *
     * @return the team's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the school's ID.
     *
     * @return the school's ID
     */
    public Long getSchool() {
        return schoolId;
    }

    /**
     * Gets the cohort's ID.
     *
     * @return the cohort's ID
     */
    public Long getCohort() {
        return cohortId;
    }

    /**
     * Gets the school's name.
     *
     * @return the school's name
     */
    public String getSchoolName() {
        return schoolName;
    }

    /**
     * Gets the cohort's name.
     *
     * @return the cohort's name
     */
    public String getCohortName() {
        return cohortName;
    }
}