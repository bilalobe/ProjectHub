package com.projecthub.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data Transfer Object for the Component entity.
 * Used for transferring component data between processes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentDTO {

    @NotNull(message = "Component ID cannot be null")
    private UUID id;

    @NotBlank(message = "Component name is mandatory")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Project ID cannot be null")
    private UUID projectId;
}