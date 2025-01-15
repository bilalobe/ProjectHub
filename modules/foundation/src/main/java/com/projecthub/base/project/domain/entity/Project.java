package com.projecthub.base.project.domain.entity;

import com.projecthub.base.milestone.domain.entity.Milestone;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.project.domain.value.ProjectDetails;
import com.projecthub.base.project.domain.value.ProjectProgress;
import com.projecthub.base.project.domain.value.ProjectTeam;
import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.task.domain.entity.Task;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Project extends BaseEntity {

    @ToString.Include
    @EqualsAndHashCode.Include
    @NotBlank(message = "Project name is mandatory")
    @Size(max = 100)
    @Column(unique = true, nullable = false)
    private String name;

    @Embedded
    private ProjectDetails details;

    @Embedded
    private ProjectProgress progress;

    @Embedded
    private ProjectTeam team;

    @ToString.Include
    @NotNull(message = "Status is mandatory")
    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.DRAFT;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Milestone> milestones = new ArrayList<>();

    @Version
    private Long version;

    public void start() {
        if (ProjectStatus.DRAFT != status) {
            throw new IllegalStateException("Only draft projects can be started");
        }
        if (null == team) {
            throw new IllegalStateException("Cannot start project without team");
        }
        this.status = ProjectStatus.ACTIVE;
        this.progress = new ProjectProgress(LocalDate.now(), null, 0, this.tasks.size());
    }

    public void complete() {
        if (ProjectStatus.ACTIVE != status) {
            throw new IllegalStateException("Only active projects can be completed");
        }
        if (this.hasIncompleteTasks()) {
            throw new IllegalStateException("Cannot complete project with incomplete tasks");
        }
        this.status = ProjectStatus.COMPLETED;
        this.progress = new ProjectProgress(this.progress.startDate(), LocalDate.now(), this.progress.completedTasks(), this.tasks.size());
    }

    public void cancel() {
        if (ProjectStatus.COMPLETED == status) {
            throw new IllegalStateException("Cannot cancel completed project");
        }
        this.status = ProjectStatus.CANCELLED;
        this.progress = new ProjectProgress(this.progress.startDate(), LocalDate.now(), this.progress.completedTasks(), this.tasks.size());
    }

    public void addTask(final Task task) {
        task.setProject(this);
        this.tasks.add(task);
    }

    public void addMilestone(final Milestone milestone) {
        milestone.setProject(this);
        this.milestones.add(milestone);
    }

    public boolean hasIncompleteTasks() {
        return this.tasks.stream().anyMatch(task -> !task.isCompleted());
    }

    public int calculateProgress() {
        if (this.tasks.isEmpty()) return 0;
        return (int) (this.tasks.stream()
            .filter(Task::isCompleted)
            .count() * 100.0 / this.tasks.size());
    }

    public boolean isActive() {
        return ProjectStatus.ACTIVE == status;
    }

    public boolean isOverdue() {
        return !this.isCompleted() && LocalDate.now().isAfter(this.details.deadline());
    }

    public boolean isCompleted() {
        return ProjectStatus.COMPLETED == status;
    }
}
