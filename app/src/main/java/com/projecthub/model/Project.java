package com.projecthub.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a project within a {@link Team}.
 * <p>
 * A project contains tasks and is associated with a specific team.
 * </p>
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    indexes = {
        @Index(name = "idx_project_name", columnList = "name")
    }
)
public class Project {

    /**
     * The unique identifier for the project.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The name of the project.
     */
    @NotBlank(message = "Project name is mandatory")
    @Size(max = 100, message = "Project name must be less than 100 characters")
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * The description of the project.
     */
    @NotBlank(message = "Description is mandatory")
    @Size(max = 500, message = "Description must be less than 500 characters")
    @Column(nullable = false)
    private String description;

    /**
     * The deadline for the project.
     */
    @NotNull(message = "Deadline is mandatory")
    private LocalDate deadline;

    /**
     * The start date of the project.
     */
    private LocalDate startDate;

    /**
     * The end date of the project.
     */
    private LocalDate endDate;

    /**
     * The status of the project.
     */
    @NotBlank(message = "Status is mandatory")
    private String status;

    /**
     * The team to which this project belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    /**
     * The list of tasks associated with this project.
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    private boolean deleted = false;

    // Default constructor required by JPA
    public Project() {}

    public Project(UUID id, String name, String description, LocalDate deadline, LocalDate startDate, LocalDate endDate, String status, Team team) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.team = team;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", deadline='" + deadline + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", team=" + (team != null ? team.getName() : "null") +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", createdBy='" + createdBy + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}