package com.projecthub.core.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object representing a project in the ProjectHub system.
 * Projects are assignments or tasks assigned to teams with specific deadlines and objectives.
 *
 * @param id Unique identifier of the project
 * @param name Display name of the project
 * @param description Detailed description of the project objectives and requirements
 * @param teamId Reference to the team assigned to this project
 * @param deadline Due date for project completion
 * @param startDate Date when the project officially begins
 * @param endDate Actual completion date of the project
 * @param status Current state of the project (e.g., "IN_PROGRESS", "COMPLETED")
 */
public record ProjectDTO(
    UUID id,
    String name,
    String description,
    UUID teamId,
    LocalDate deadline,
    LocalDate startDate,
    LocalDate endDate,
    String status
) {}