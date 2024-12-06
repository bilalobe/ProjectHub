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
     * Custom constructor for creating a TaskDTO with specific values.
     *
     * @param id             the task ID
     * @param name           the task name
     * @param description    the task description
     * @param status         the task status
     * @param dueDate        the task due date
     * @param projectId      the project ID
     * @param assignedUserId the assigned user ID
     * @param createdAt      the creation timestamp
     * @param createdBy      the creator's username
     * @param deleted        the deletion status
     * @param updatedAt      the last updated timestamp
     */
    public TaskDTO(UUID id, String name, String description, String status, LocalDate dueDate, UUID projectId, UUID assignedUserId, LocalDate createdAt, String createdBy, boolean deleted, LocalDate updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.projectId = projectId;
        this.assignedUserId = assignedUserId;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.deleted = deleted;
        this.updatedAt = updatedAt;
    }

    // Getters and setters...

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(UUID assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}