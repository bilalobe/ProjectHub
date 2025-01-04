package com.projecthub.base.milestone.domain.entity;

import com.projecthub.base.milestone.domain.enums.MilestoneStatus;
import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.task.domain.entity.Task;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a project milestone with associated tasks and dependencies.
 *
 * <p>
 * This entity includes details such as name, description, due date, status,
 * progress, and relationships to projects, tasks, and other milestones.
 * </p>
 *
 * <p>
 * Key functionalities:
 * <ul>
 * <li>Check if the milestone is overdue.</li>
 * <li>Update progress and handle status transitions.</li>
 * <li>Manage dependencies between milestones.</li>
 * <li>Start, complete, block, or cancel the milestone.</li>
 * <li>Determine if the milestone is active or blocked.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Constraints:
 * <ul>
 * <li>Name must be between 3 and 100 characters and not blank.</li>
 * <li>Description can be up to 500 characters.</li>
 * <li>Due date must be in the future.</li>
 * <li>Progress must be between 0 and 100.</li>
 * <li>Cannot have circular dependencies.</li>
 * <li>Cannot complete if there are incomplete tasks.</li>
 * <li>Cannot block or cancel if already completed or cancelled.</li>
 * </ul>
 * </p>
 *
 * @author
 */
@Entity
@Table(name = "milestones")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Milestone extends BaseEntity {

    @ToString.Include
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.PROTECTED)
    private MilestoneStatus status = MilestoneStatus.PENDING;

    @Version
    private Long version;

    @Min(0)
    @Max(100)
    private Integer progress = 0;

    @OneToMany(mappedBy = "milestone", cascade = CascadeType.ALL)
    private Set<Task> tasks = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "milestone_dependencies", joinColumns = @JoinColumn(name = "milestone_id"), inverseJoinColumns = @JoinColumn(name = "dependency_id"))
    private Set<Milestone> dependencies = new HashSet<>();
    private MilestoneStatus targetStatus;

    public boolean isOverdue() {
        return status != MilestoneStatus.COMPLETED && dueDate.isBefore(LocalDate.now());
    }

    public void updateProgress(int newProgress) {
        if (newProgress < 0 || newProgress > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100");
        }
        this.progress = newProgress;
        if (newProgress == 100) {
            this.status = MilestoneStatus.COMPLETED;
        }
    }

    public void addDependency(Milestone dependency) {
        if (dependency.equals(this)) {
            throw new IllegalArgumentException("Milestone cannot depend on itself");
        }
        dependencies.add(dependency);
    }

    public boolean canStart() {
        return dependencies.stream()
            .allMatch(m -> m.status == MilestoneStatus.COMPLETED || m.status == MilestoneStatus.CANCELLED);
    }

    public void start() {
        if (!canStart()) {
            throw new IllegalStateException("Dependencies not met");
        }
        if (status != MilestoneStatus.PENDING) {
            throw new IllegalStateException("Can only start pending milestones");
        }
        status = MilestoneStatus.IN_PROGRESS;
    }

    public boolean isBlocked() {
        return status == MilestoneStatus.BLOCKED;
    }

    public void complete() {
        if (!tasks.isEmpty() && tasks.stream().anyMatch(t -> !t.isCompleted())) {
            throw new IllegalStateException("Cannot complete milestone with incomplete tasks");
        }
        status = MilestoneStatus.COMPLETED;
        progress = 100;
    }

    public void block() {
        if (status == MilestoneStatus.COMPLETED || status == MilestoneStatus.CANCELLED) {
            throw new IllegalStateException("Cannot block completed or cancelled milestone");
        }
        status = MilestoneStatus.BLOCKED;
    }

    public void cancel() {
        if (status == MilestoneStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed milestone");
        }
        status = MilestoneStatus.CANCELLED;
    }

    public boolean isActive() {
        return status == MilestoneStatus.IN_PROGRESS;
    }

    public boolean hasIncompleteTasks() {
        return tasks.stream().anyMatch(task -> !task.isCompleted());
    }

    public boolean hasCyclicDependencies() {
        Set<Milestone> visited = new HashSet<>();
        Set<Milestone> recursionStack = new HashSet<>();
        return checkCyclicDependencies(this, visited, recursionStack);
    }

    private boolean checkCyclicDependencies(Milestone milestone,
                                            Set<Milestone> visited,
                                            Set<Milestone> recursionStack) {
        if (recursionStack.contains(milestone)) {
            return true;
        }
        if (visited.contains(milestone)) {
            return false;
        }

        visited.add(milestone);
        recursionStack.add(milestone);

        boolean hasCycle = milestone.dependencies.stream()
            .anyMatch(dep -> checkCyclicDependencies(dep, visited, recursionStack));

        recursionStack.remove(milestone);
        return hasCycle;
    }

    public boolean isCompleted() {
        return status == MilestoneStatus.COMPLETED;
    }

    public MilestoneStatus getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(MilestoneStatus status) {
        this.targetStatus = status;
    }
}
