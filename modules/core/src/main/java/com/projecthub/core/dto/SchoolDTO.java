package com.projecthub.core.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object representing a school in the ProjectHub system.
 * Schools are the top-level organizational units that contain cohorts and teams.
 *
 * @param id Unique identifier of the school
 * @param name Official name of the school
 * @param address Physical location of the school
 * @param teamIds List of team identifiers associated with this school
 * @param cohortIds List of cohort identifiers associated with this school
 * @param createdAt Timestamp when the school record was created
 * @param updatedAt Timestamp of the last update to the school record
 * @param createdBy Username of the user who created the school record
 * @param deleted Soft deletion flag
 */
public record SchoolDTO(
    UUID id,
    String name,
    String address,
    List<UUID> teamIds,
    List<UUID> cohortIds,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    boolean deleted
) {}