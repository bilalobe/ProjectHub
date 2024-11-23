package com.projecthub.dto;

import com.projecthub.model.School;

/**
 * Data Transfer Object for the School entity.
 * Used for transferring school data between processes.
 */
public class SchoolSummary {
    private final Long id;
    private final String name;

    /**
     * Default constructor.
     */
    public SchoolSummary() {
        this.id = null;
        this.name = null;
    }

    /**
     * Constructs a SchoolSummary with specified values.
     *
     * @param id   the school ID
     * @param name the school's name
     */
    public SchoolSummary(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructs a SchoolSummary from a School entity.
     *
     * @param school the School entity
     */
    public SchoolSummary(School school) {
        this.id = school.getId();
        this.name = school.getName();
    }

    /**
     * Gets the school ID.
     *
     * @return the school ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the school's name.
     *
     * @return the school's name
     */
    public String getName() {
        return name;
    }
}