package com.projecthub.base.cohort.api.dto;

import java.util.UUID;

/**
 * Data Transfer Object representing a cohort in the ProjectHub system.
 * A cohort represents a group or class of students within a school.
 *
 * @param id       Unique identifier of the cohort
 * @param name     Display name of the cohort
 * @param schoolId Reference to the school this cohort belongs to
 */
public record CohortDTO(
    UUID id,
    String name,
    UUID schoolId
) {
}
