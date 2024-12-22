package com.projecthub.core.dto;

import com.projecthub.core.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @NotNull(message = "Status is mandatory")
    private TaskStatus status;

    /**
     * Gets the status of the task.
     *
     * @return the current status of the task
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the task.
     *
     * @param status the new status of the task
     */
    public void setStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }
}