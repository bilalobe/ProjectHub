package com.projecthub.dto;

import com.projecthub.model.Task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for the Task entity.
 * Used for transferring task data between processes.
 */
public class TaskSummary {
    private final Long id;

    @NotBlank(message = "Task name is mandatory")
    @Size(max = 100, message = "Task name cannot exceed 100 characters")
    private final String name;

    @Size(max = 500, message = "Task description cannot exceed 500 characters")
    private final String description;

    @NotBlank(message = "Status is mandatory")
    private final String status;

    private final String dueDate;

    private final Long assignedUserId;

    @NotNull(message = "Project ID is mandatory")
    private final Long projectId;

    /**
     * Default constructor.
     */
    public TaskSummary() {
        this.id = null;
        this.name = null;
        this.description = null;
        this.status = null;
        this.dueDate = null;
        this.assignedUserId = null;
        this.projectId = null;
    }

    /**
     * Constructs a TaskSummary from a Task entity.
     *
     * @param task the Task entity
     */
    public TaskSummary(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.dueDate = task.getDueDate() != null ? task.getDueDate().toString() : null;
        this.assignedUserId = task.getAssignedUser() != null ? task.getAssignedUser().getId() : null;
        this.projectId = task.getProject() != null ? task.getProject().getId() : null;
    }

    // Getters with JavaDoc comments
    /**
     * Gets the task ID.
     *
     * @return the task ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the task name.
     *
     * @return the task name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the task status.
     *
     * @return the task status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the task due date.
     *
     * @return the task due date
     */
    public String getDueDate() {
        return dueDate;
    }

    /**
     * Gets the assigned user ID.
     *
     * @return the assigned user ID
     */
    public Long getAssignedUser() {
        return assignedUserId;
    }

    /**
     * Gets the project ID.
     *
     * @return the project ID
     */
    public Long getProject() {
        return projectId;
    }
}