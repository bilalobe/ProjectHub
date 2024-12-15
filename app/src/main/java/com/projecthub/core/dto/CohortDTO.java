package com.projecthub.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data Transfer Object for the Cohort entity.
 * Used for transferring cohort data between processes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CohortDTO {

    @NotNull(message = "Cohort ID cannot be null")
    private UUID id;

    @NotBlank(message = "Cohort name is mandatory")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @NotNull(message = "School ID cannot be null")
    private UUID schoolId;
}