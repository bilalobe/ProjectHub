package com.projecthub.dto;

import com.projecthub.model.Task;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents a summary of a task with its essential details.
 * 
 * <p>This class encapsulates the properties of a task, including its ID, name, description, status,
 * and due date. It provides JavaFX properties for easy binding and observation within the UI.
 * 
 * <p>There are two constructors available:
 * <ul>
 *   <li>{@link #TaskSummary(Long, String, String, String, LocalDate, Long, Long)}: Initializes a TaskSummary with specified values.</li>
 *   <li>{@link #TaskSummary(Task)}: Initializes a TaskSummary from a Task object.</li>
 * </ul>
 * 
 * <p>Property accessors are provided for each field:
 * <ul>
 *   <li>{@link #idProperty()}: Retrieves the ID property.</li>
 *   <li>{@link #nameProperty()}: Retrieves the name property.</li>
 *   <li>{@link #descriptionProperty()}: Retrieves the description property.</li>
 *   <li>{@link #statusProperty()}: Retrieves the status property.</li>
 *   <li>{@link #dueDateProperty()}: Retrieves the due date property.</li>
 *   <li>{@link #projectProperty()}: Retrieves the project property.</li>
 *   <li>{@link #assignedUserProperty()}: Retrieves the assigned user property.</li>
 * </ul>
 */
public class TaskSummary {

    private final LongProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final StringProperty status;
    private final ObjectProperty<LocalDate> dueDate;
    private final LongProperty project;
    private final LongProperty assignedUser;

    public TaskSummary(Long id, String name, String description, String status, LocalDate dueDate, Long project, Long assignedUser) {
        if (id == null || name == null || description == null || status == null || dueDate == null || project == null || assignedUser == null) {
            throw new IllegalArgumentException("None of the parameters can be null");
        }
        this.id = new SimpleLongProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.status = new SimpleStringProperty(status);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.project = new SimpleLongProperty(project);
        this.assignedUser = new SimpleLongProperty(assignedUser);
    }

    public TaskSummary(Task task) {
        this.id = new SimpleLongProperty(task.getId());
        this.name = new SimpleStringProperty(task.getName());
        this.description = new SimpleStringProperty(task.getDescription());
        this.status = new SimpleStringProperty(task.getStatus());
        this.dueDate = new SimpleObjectProperty<>(task.getDueDate());
        this.project = new SimpleLongProperty(Optional.ofNullable(task.getProject()).map(p -> p.getId()).orElse(0L));
        this.assignedUser = new SimpleLongProperty(Optional.ofNullable(task.getAssignedUser()).map(u -> u.getId()).orElse(0L));
    }

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

    public ObjectProperty<LocalDate> dueDateProperty() {
        return dueDate;
    }

    public LongProperty projectProperty() {
        return project;
    }

    public LongProperty assignedUserProperty() {
        return assignedUser;
    }

    public Long getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getDescription() {
        return description.get();
    }

    public String getStatus() {
        return status.get();
    }

    public LocalDate getDueDate() {
        return dueDate.get();
    }

    public Long getProject() {
        return project.get();
    }

    public Long getAssignedUser() {
        return assignedUser.get();
    }
}