package com.projecthub.dto;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for Task entity.
 * Used for transferring task data between processes.
 */
public class TaskDTO {

    /**
     * Unique identifier for the task.
     */
    @CsvBindByName
    @NotNull(message = "Task ID cannot be null")
    private UUID id;

    /**
     * Name of the task.
     */
    @CsvBindByName
    @NotBlank(message = "Task name is mandatory")
    @Size(max = 255, message = "Task name must be less than 255 characters")
    private String name;

    /**
     * Description of the task.
     */
    @CsvBindByName
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    /**
     * Status of the task.
     */
    @CsvBindByName
    @NotNull(message = "Status cannot be null")
    private String status;

    /**
     * Due date of the task.
     */
    @CsvBindByName
    private LocalDate dueDate;

    /**
     * Identifier of the project associated with the task.
     */
    @CsvBindByName
    private UUID projectId;

    /**
     * Identifier of the user assigned to the task.
     */
    @CsvBindByName
    private UUID assignedUserId;

    /**
     * Timestamp when the task was created.
     */
    @CsvBindByName
    private LocalDate createdAt;

    /**
     * Username of the creator of the task.
     */
    @CsvBindByName
    private String createdBy;

    /**
     * Flag indicating whether the task is deleted.
     */
    @CsvBindByName
    private boolean deleted;

    /**
     * Timestamp when the task was last updated.
     */
    @CsvBindByName
    private LocalDate updatedAt;

    /**
     * Default constructor.
     */
    public TaskDTO() {
    }

    /**
     * Gets the unique identifier for the task.
     *
     * @return the task ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the task.
     *
     * @param id the task ID
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Gets the name of the task.
     *
     * @return the task name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the task.
     *
     * @param name the task name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the task.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the task.
     *
     * @param description the task description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the status of the task.
     *
     * @return the task status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the task.
     *
     * @param status the task status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the due date of the task.
     *
     * @return the task due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date of the task.
     *
     * @param dueDate the task due date
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the project identifier associated with the task.
     *
     * @return the project ID
     */
    public UUID getProjectId() {
        return projectId;
    }

    /**
     * Sets the project identifier associated with the task.
     *
     * @param projectId the project ID
     */
    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    /**
     * Gets the user identifier assigned to the task.
     *
     * @return the assigned user ID
     */
    public UUID getAssignedUserId() {
        return assignedUserId;
    }

    /**
     * Sets the user identifier assigned to the task.
     *
     * @param assignedUserId the assigned user ID
     */
    public void setAssignedUserId(UUID assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    /**
     * Gets the creation timestamp of the task.
     *
     * @return the creation timestamp
     */
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the task.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the username of the task creator.
     *
     * @return the creator's username
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the username of the task creator.
     *
     * @param createdBy the creator's username
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Checks if the task is marked as deleted.
     *
     * @return true if deleted, false otherwise
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Sets the deletion status of the task.
     *
     * @param deleted true to mark as deleted, false otherwise
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Gets the timestamp when the task was last updated.
     *
     * @return the last updated timestamp
     */
    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when the task was last updated.
     *
     * @param updatedAt the last updated timestamp
     */
    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}