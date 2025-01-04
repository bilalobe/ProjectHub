package com.projecthub.base.component.api.dto;

import java.util.UUID;

/**
 * Data Transfer Object representing a project component in the ProjectHub system.
 * Components are distinct parts or modules that make up a project.
 *
 * @param id          Unique identifier of the component
 * @param name        Display name of the component
 * @param description Detailed description of the component's purpose and requirements
 * @param projectId   Reference to the project this component belongs to
 */
public record ComponentDTO(
    UUID id,
    String name,
    String description,
    UUID projectId
) {
}
