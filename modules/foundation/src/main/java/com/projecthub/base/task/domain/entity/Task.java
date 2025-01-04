package com.projecthub.base.task.domain.entity;


import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.task.domain.enums.TaskStatus;
import com.projecthub.base.task.domain.value.TaskValue;
import com.projecthub.base.user.domain.entity.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

/**
 * Represents a task within a {@link Project}.
 * <p>
 * Tasks are atomic units of work within a project that can be assigned to specific users.
 * Each task has a status, optional due date, and can be assigned to a team member.
 * </p>
 * <p>
 * Business rules:
 * <ul>
 *   <li>Tasks must have a name and status</li>
 *   <li>Due dates, if specified, must be in the future</li>
 *   <li>Tasks must belong to exactly one project</li>
 *   <li>Tasks can optionally be assigned to a user</li>
 * </ul>
 * </p>
 *
 * @see Project
 * @see AppUser
 * @see TaskStatus
 */
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    indexes = {
        @Index(name = "idx_task_name", columnList = "name")
    }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "project")
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
     * <p>
     * Optional field providing additional details about the task requirements,
     * acceptance criteria, or other relevant information.
     * </p>
     */
    private String description;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer priority = 3;

    @Min(0)
    private Integer estimatedHours;

    @Column(nullable = false)
    private boolean isOverdue = false;

    @Column(nullable = false)
    private boolean isBlocked = false;

    private String blockedReason;

    /**
     * The current status of the task.
     * <p>
     * Represents the task's progress state. Must be one of the predefined
     * statuses in {@link TaskStatus}.
     * </p>
     */
    @NotNull(message = "Status is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    /**
     * The due date of the task.
     */
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    /**
     * The user assigned to this task.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private AppUser assignedUser;

    /**
     * The project to which this task belongs.
     */
    @NotNull(message = "Project is mandatory")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Project project;

    @PrePersist
    @PreUpdate
    protected void validateDates() {
        if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
            throw new IllegalStateException("Due date cannot be in the past");
        }
    }

    public boolean isCompleted() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isCompleted'");
    }

    public TaskValue getValue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getValue'");
    }
}
