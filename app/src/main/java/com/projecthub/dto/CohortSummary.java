package com.projecthub.dto;

import com.projecthub.model.Cohort;
import com.projecthub.model.School;

/**
 * Data Transfer Object for the Cohort entity.
 * Used for transferring cohort data between processes.
 */
public class CohortSummary {
    private final Long id;
    private final String name;
    private final School school;

    /**
     * Default constructor.
     */
    public CohortSummary() {
        this.id = null;
        this.name = null;
        this.school = null;
    }

    /**
     * Constructs a CohortSummary with specified values.
     *
     * @param id     the cohort ID
     * @param name   the cohort's name
     * @param school the associated School
     */
    public CohortSummary(Long id, String name, School school) {
        this.id = id;
        this.name = name;
        this.school = school;
    }

    /**
     * Constructs a CohortSummary from a Cohort entity.
     *
     * @param cohort the Cohort entity
     */
    public CohortSummary(Cohort cohort) {
        this.id = cohort.getId();
        this.name = cohort.getName();
        this.school = cohort.getSchool();
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
     * Gets the associated School.
     *
     * @return the associated School
     */
    public School getSchool() {
        return school;
    }
}