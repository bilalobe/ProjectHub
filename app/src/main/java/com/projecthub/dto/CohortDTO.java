package com.projecthub.dto;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

/**
 * Data Transfer Object for the Cohort entity.
 * Used for transferring cohort data between processes.
 */
public class CohortDTO {

    /**
     * Unique identifier for the cohort.
     */
    @CsvBindByName(column = "id")
    @NotNull(message = "Cohort ID cannot be null")
    private UUID id;

    /**
     * Name of the cohort.
     */
    @CsvBindByName(column = "name")
    @NotBlank(message = "Cohort name is mandatory")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    /**
     * School identifier associated with the cohort.
     */
    @CsvBindByName(column = "schoolId")
    @NotNull(message = "School ID cannot be null")
    private UUID schoolId;

    /**
     * Default constructor.
     */
    public CohortDTO() {
    }

    /**
     * Constructs a CohortDTO with specified values.
     *
     * @param id     the cohort ID
     * @param name   the cohort's name
     * @param schoolId the associated School ID
     */
    public CohortDTO(UUID id, String name, UUID schoolId) {
        this.id = id;
        this.name = name;
        this.schoolId = schoolId;
    }

    /**
     * Gets the unique identifier for the cohort.
     *
     * @return the cohort ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the cohort.
     *
     * @param id the cohort ID
     */
    public void setId(UUID id) {
        this.id = id;
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
     * Sets the cohort's name.
     *
     * @param name the cohort's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the associated School ID.
     *
     * @return the associated School ID
     */
    public UUID getSchoolId() {
        return schoolId;
    }

    /**
     * Sets the associated School ID.
     *
     * @param schoolId the associated School ID
     */
    public void setSchoolId(UUID schoolId) {
        this.schoolId = schoolId;
    }
}