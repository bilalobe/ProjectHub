package com.projecthub.dto;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

/**
 * Data Transfer Object for the Component entity.
 * Used for transferring component data between processes.
 */
public class ComponentDTO {

    /**
     * Unique identifier for the component.
     */
    @CsvBindByName(column = "id")
    @NotNull(message = "Component ID cannot be null")
    private UUID id;

    /**
     * Name of the component.
     */
    @CsvBindByName(column = "name")
    @NotBlank(message = "Component name is mandatory")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    /**
     * Description of the component.
     */
    @CsvBindByName(column = "description")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    /**
     * Project identifier associated with the component.
     */
    @CsvBindByName(column = "projectId")
    @NotNull(message = "Project ID cannot be null")
    private UUID projectId;

    /**
     * No-argument constructor.
     */
    public ComponentDTO() {
        // Default constructor
    }

    /**
     * Custom constructor for creating a ComponentDTO with specific values.
     *
     * @param id          the component ID
     * @param name        the component name
     * @param description the component description
     * @param projectId   the project ID
     */
    public ComponentDTO(UUID id, String name, String description, UUID projectId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.projectId = projectId;
    }

    /**
     * Gets the unique identifier for the component.
     *
     * @return the component ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the component.
     *
     * @param id the component ID
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Gets the name of the component.
     *
     * @return the component's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the component.
     *
     * @param name the component name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the component.
     *
     * @return the component's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the component.
     *
     * @param description the component description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the project identifier associated with the component.
     *
     * @return the project ID
     */
    public UUID getProjectId() {
        return projectId;
    }

    /**
     * Sets the project identifier associated with the component.
     *
     * @param projectId the project ID
     */
    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
}