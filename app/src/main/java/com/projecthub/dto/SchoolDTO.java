package com.projecthub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for the School entity.
 * Used for transferring school data between processes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolDTO {

    @NotNull(message = "School ID cannot be null")
    private UUID id;

    @NotBlank(message = "School name is mandatory")
    private String name;

    @Size(max = 255, message = "Address must be less than 255 characters")
    private String address;

    private List<UUID> teamIds;

    private List<UUID> cohortIds;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private boolean deleted;
}