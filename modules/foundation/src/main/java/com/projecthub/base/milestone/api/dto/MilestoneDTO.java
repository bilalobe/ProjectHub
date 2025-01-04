package com.projecthub.base.milestone.api.dto;

import com.projecthub.base.milestone.domain.enums.MilestoneStatus;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record MilestoneDTO(
    UUID id,
    String name,
    String description,
    LocalDate dueDate,
    UUID projectId,
    Set<UUID> dependencyIds,
    Integer progress,
    MilestoneStatus status
) {
}
