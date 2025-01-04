package com.projecthub.base.team.api.dto;

import java.util.UUID;

/**
 * Data Transfer Object representing a team in the ProjectHub system.
 * Teams are groups of students working together on projects.
 *
 * @param id       Unique identifier of the team
 * @param name     Display name of the team
 * @param schoolId Reference to the associated school
 * @param cohortId Reference to the associated cohort
 */
public record TeamDTO(
    UUID id,
    String name,
    UUID schoolId,
    UUID cohortId
) {
}
