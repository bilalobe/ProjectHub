package com.projecthub.dto;

/**
 * Data Transfer Object for the Cohort entity.
 * Used for transferring cohort data between processes.
 */
public class CohortSummary {
    private final Long id;
    private final String name;
    private final Long schoolId;

    /**
     * Default constructor.
     */
    public CohortSummary() {
        this.id = null;
        this.name = null;
        this.schoolId = null;
    }

    /**
     * Constructs a CohortSummary with specified values.
     *
     * @param id     the cohort ID
     * @param name   the cohort's name
     * @param schoolId the associated School ID
     */
    public CohortSummary(Long id, String name, Long schoolId) {
        this.id = id;
        this.name = name;
        this.schoolId = schoolId;
    }

    /**
     * Gets the cohort ID.
     *
     * @return the cohort ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the cohort's name.
     *
     * @return the cohort's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the associated School ID.
     *
     * @return the associated School ID
     */
    public Long getSchoolId() {
        return schoolId;
    }
}