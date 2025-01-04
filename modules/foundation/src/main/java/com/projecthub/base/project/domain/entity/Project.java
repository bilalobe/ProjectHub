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
        if (status != ProjectStatus.DRAFT) {
            throw new IllegalStateException("Only draft projects can be started");
        }
        if (team == null) {
            throw new IllegalStateException("Cannot start project without team");
        }
        status = ProjectStatus.ACTIVE;
        progress = new ProjectProgress(LocalDate.now(), null, 0, tasks.size());
    }

    public void complete() {
        if (status != ProjectStatus.ACTIVE) {
            throw new IllegalStateException("Only active projects can be completed");
        }
        if (hasIncompleteTasks()) {
            throw new IllegalStateException("Cannot complete project with incomplete tasks");
        }
        status = ProjectStatus.COMPLETED;
        progress = new ProjectProgress(progress.startDate(), LocalDate.now(), progress.completedTasks(), tasks.size());
    }

    public void cancel() {
        if (status == ProjectStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed project");
        }
        status = ProjectStatus.CANCELLED;
        progress = new ProjectProgress(progress.startDate(), LocalDate.now(), progress.completedTasks(), tasks.size());
    }

    public void addTask(Task task) {
        task.setProject(this);
        tasks.add(task);
    }

    public void addMilestone(Milestone milestone) {
        milestone.setProject(this);
        milestones.add(milestone);
    }

    public boolean hasIncompleteTasks() {
        return tasks.stream().anyMatch(task -> !task.isCompleted());
    }

    public int calculateProgress() {
        if (tasks.isEmpty()) return 0;
        return (int) (tasks.stream()
            .filter(Task::isCompleted)
            .count() * 100.0 / tasks.size());
    }

    public boolean isActive() {
        return status == ProjectStatus.ACTIVE;
    }

    public boolean isOverdue() {
        return !isCompleted() && LocalDate.now().isAfter(details.deadline());
    }

    public boolean isCompleted() {
        return status == ProjectStatus.COMPLETED;
    }
}
