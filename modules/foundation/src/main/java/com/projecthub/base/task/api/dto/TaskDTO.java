package com.projecthub.base.task.api.dto;

import com.projecthub.base.task.domain.enums.TaskStatus;

import java.util.UUID;

/**
 * Data Transfer Object representing a task in the ProjectHub system.
 * Tasks are individual units of work within a project.
 *
 * @param id          Unique identifier of the task
 * @param name        Display name of the task
 * @param description Detailed description of the task requirements
 * @param status      Current state of the task in its lifecycle
 */
public record TaskDTO(
    UUID id,
    String name,
    String description,
    TaskStatus status
) {
}
