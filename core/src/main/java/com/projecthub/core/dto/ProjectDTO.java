package com.projecthub.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for the Project entity.
 * Used for transferring project data between processes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

    @NotNull(message = "Project ID cannot be null")
    private UUID id;

    @NotBlank(message = "Project name is mandatory")
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Team ID cannot be null")
    private UUID teamId;

    private LocalDate deadline;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;
}