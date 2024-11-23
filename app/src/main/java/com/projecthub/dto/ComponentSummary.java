package com.projecthub.dto;

import com.projecthub.model.Component;

/**
 * Data Transfer Object for the Component entity.
 * Used for transferring component data between processes.
 */
public class ComponentSummary {
    private final Long id;
    private final String name;
    private final String description;
    private final Long projectId;

    /**
     * Default constructor.
     */
    public ComponentSummary() {
        this.id = 0L;
        this.name = "";
        this.description = "";
        this.projectId = 0L;
    }

    /**
     * Constructs a ComponentSummary with specified values.
     *
     * @param id          the component ID
     * @param name        the component's name
     * @param description the component's description
     * @param projectId   the project ID
     */
    public ComponentSummary(Long id, String name, String description, Long projectId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.projectId = projectId;
    }

    /**
     * Constructs a ComponentSummary from a Component entity.
     *
     * @param component the Component entity
     */
    public ComponentSummary(Component component) {
        this.id = component.getId();
        this.name = component.getName();
        this.description = component.getDescription();
        this.projectId = component.getProject() != null ? component.getProject().getId() : null;
    }

    /**
     * Gets the component ID.
     *
     * @return the component ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the component's name.
     *
     * @return the component's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the component's description.
     *
     * @return the component's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the project ID.
     *
     * @return the project ID
     */
    public Long getProjectId() {
        return projectId;
    }
}