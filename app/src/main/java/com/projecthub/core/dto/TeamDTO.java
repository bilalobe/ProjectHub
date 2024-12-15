package com.projecthub.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data Transfer Object for the Team entity.
 * Used for transferring team data between processes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {

    @NotNull(message = "Team ID cannot be null")
    private UUID id;

    @NotBlank(message = "Team name is mandatory")
    private String name;

    @NotNull(message = "School ID cannot be null")
    private UUID schoolId;

    @NotNull(message = "Cohort ID cannot be null")
    private UUID cohortId;
}