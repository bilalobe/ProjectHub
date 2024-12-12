package com.projecthub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

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
@Getter
@Setter
public class Project extends BaseEntity {

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
    @JoinColumn(nullable = false)
    private Team team;

    /**
     * The list of tasks associated with this project.
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    /**
     * Default constructor required by JPA.
     */
    public Project() {
    }

    public Project(String name, String description, LocalDate deadline, LocalDate startDate, LocalDate endDate, String status, Team team) {
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.team = team;
    }
}