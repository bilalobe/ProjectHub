package com.projecthub.dto;

import com.projecthub.model.School;

/**
 * Data Transfer Object for the School entity.
 * Used for transferring school data between processes.
 */
public class SchoolSummary {
    private final Long id;
    private final String name;
    private final String address;

    /**
     * Default constructor.
     */
    public SchoolSummary() {
        this.id = null;
        this.name = null;
        this.address = "";
    }

    /**
     * Constructs a SchoolSummary with specified values.
     *
     * @param id   the school ID
     * @param name the school's name
     * @param address the school's address
     */
    public SchoolSummary(Long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    /**
     * Constructs a SchoolSummary from a School entity.
     *
     * @param school the School entity
     */
    public SchoolSummary(School school) {
        this.id = school.getId();
        this.name = school.getName();
        this.address = school.getAddress();
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

    /**
     * Gets the school's address.
     *
     * @return the school's address
     */
    public String getAddress() {
        return address;
    }
}