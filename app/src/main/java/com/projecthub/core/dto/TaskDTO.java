package com.projecthub.core.dto;

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

    /**
     * Unique identifier for the task.
     */
    @NotNull(message = "Task ID cannot be null")
    private UUID id;

    /**
     * Name of the task.
     */
    @NotBlank(message = "Task name is mandatory")
    @Size(max = 255, message = "Task name must be less than 255 characters")
    private String name;

    /**
     * Description of the task.
     */
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    /**
     * Status of the task.
     */
    @NotNull(message = "Status cannot be null")
    private TaskStatus status;

    /**
     * Due date of the task.
     */
    private LocalDate dueDate;

    /**
     * Identifier of the project to which the task belongs.
     */
    private UUID projectId;

    /**
     * Identifier of the user to whom the task is assigned.
     */
    private UUID assignedUserId;

    /**
     * Timestamp when the task was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the task was last updated.
     */
    private LocalDateTime updatedAt;

    /**
     * Identifier of the user who created the task.
     */
    private String createdBy;

    /**
     * Flag indicating whether the task is deleted.
     */
    private boolean deleted;

    /**
     * Enum representing the status of the task.
     */
    public enum TaskStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }
}