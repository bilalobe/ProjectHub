package com.projecthub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a task within a {@link Project}.
 * <p>
 * Tasks are units of work assigned to users.
 * </p>
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        indexes = {
                @Index(name = "idx_task_name", columnList = "name")
        }
)
@Getter
@Setter
public class Task extends BaseEntity {

    /**
     * The name of the task.
     */
    @NotBlank(message = "Task name is mandatory")
    @Size(max = 100, message = "Task name must be less than 100 characters")
    @Column(nullable = false)
    private String name;

    /**
     * The description of the task.
     */
    private String description;

    /**
     * The status of the task.
     */
    @NotBlank(message = "Status is mandatory")
    @Size(max = 50, message = "Status must be less than 50 characters")
    @Column(nullable = false)
    private String status;

    /**
     * The due date of the task.
     */
    private LocalDate dueDate;

    /**
     * The user assigned to this task.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private AppUser assignedUser;

    /**
     * The project to which this task belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Project project;

    // Default constructor required by JPA
    public Task() {
    }

    /**
     * Constructs a new task with the specified fields.
     */
    public Task(String name, String description, String status, LocalDate dueDate, AppUser assignedUser, Project project) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.assignedUser = assignedUser;
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(getId(), task.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}