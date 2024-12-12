package com.projecthub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for the Task entity.
 * Used for transferring task data between processes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    @NotNull(message = "Task ID cannot be null")
    private UUID id;

    @NotBlank(message = "Task name is mandatory")
    @Size(max = 255, message = "Task name must be less than 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Status cannot be null")
    private String status;

    private LocalDate dueDate;

    private UUID projectId;

    private UUID assignedUserId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private boolean deleted;
}