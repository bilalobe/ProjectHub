package com.projecthub.dto;

import com.projecthub.model.Task;
import javafx.beans.property.*;

public class TaskSummary {
    private final LongProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final StringProperty status;
    private final StringProperty dueDate;
    private final LongProperty assignedUserId;
    private final LongProperty projectId;

    public TaskSummary() {
        this.id = new SimpleLongProperty();
        this.name = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.status = new SimpleStringProperty();
        this.dueDate = new SimpleStringProperty();
        this.assignedUserId = new SimpleLongProperty();
        this.projectId = new SimpleLongProperty();
    }

    public TaskSummary(Task task) {
        this();
        this.id.set(task.getId());
        this.name.set(task.getName());
        this.description.set(task.getDescription());
        this.status.set(task.getStatus());
        this.dueDate.set(task.getDueDate() != null ? task.getDueDate().toString() : null);
        this.assignedUserId.set(task.getAssignedUser() != null ? task.getAssignedUser().getId() : null);
        this.projectId.set(task.getProject() != null ? task.getProject().getId() : null);
    }

    // Getters and Setters for properties
    public LongProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty dueDateProperty() {
        return dueDate;
    }

    public LongProperty assignedUserIdProperty() {
        return assignedUserId;
    }

    public LongProperty projectIdProperty() {
        return projectId;
    }

    // Getters and Setters for values
    public Long getId() {
        return id.get();
    }

    public void setId(Long id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getDueDate() {
        return dueDate.get();
    }

    public void setDueDate(String dueDate) {
        this.dueDate.set(dueDate);
    }

    public Long getAssignedUserId() {
        return assignedUserId.get();
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId.set(assignedUserId);
    }

    public Long getProjectId() {
        return projectId.get();
    }

    public void setProjectId(Long projectId) {
        this.projectId.set(projectId);
    }
}