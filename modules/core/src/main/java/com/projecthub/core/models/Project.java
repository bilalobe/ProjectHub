package com.projecthub.core.models;

import com.projecthub.core.entities.BaseEntity;
import com.projecthub.core.enums.ProjectStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Represents a project within the ProjectHub system.
 * <p>
 * A project is a container for tasks and components that represents a unit of work
 * assigned to a team. Each project has a defined lifecycle with start and end dates,
 * a deadline, and a current status.
 * </p>
 * <p>
 * Business rules:
 * <ul>
 *   <li>Each project must have a unique name</li>
 *   <li>Projects must be assigned to exactly one team</li>
 *   <li>Project deadlines are mandatory and must be in the future</li>
 *   <li>End date, if set, must be after the start date</li>
 * </ul>
 * </p>
 *
 * @see Team
 * @see Task
 * @see Component
 * @see ProjectStatus
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"tasks", "team"})
public class Project extends BaseEntity {

    /**
     * The unique name of the project.
     * <p>
     * The name serves as a business identifier and must be unique across all projects.
     * Maximum length is 100 characters.
     * </p>
     */
    @NotBlank(message = "Project name is mandatory")
    @Size(max = 100, message = "Project name must be less than 100 characters")
    @Column(unique = true, nullable = false)
    private String name;
    /**
     * The detailed description of the project.
     * <p>
     * Provides information about the project's goals, scope, and any other relevant details.
     * Maximum length is 500 characters.
     * </p>
     */
    @NotBlank(message = "Description is mandatory")
    @Size(max = 500, message = "Description must be less than 500 characters")
    @Column(nullable = false)
    private String description;
    /**
     * The deadline by which the project must be completed.
     * <p>
     * This is a mandatory field and represents the final date by which all project
     * deliverables must be completed. The deadline should be after the start date
     * if one is specified.
     * </p>
     */
    @NotNull(message = "Deadline is mandatory")
    private LocalDate deadline;
    /**
     * The actual or planned start date of the project.
     * <p>
     * Optional field that indicates when work on the project began or is scheduled
     * to begin. If set, must be before both the end date and deadline.
     * </p>
     */
    private LocalDate startDate;
    /**
     * The actual or expected end date of the project.
     * <p>
     * Optional field that indicates when the project was or is expected to be completed.
     * If set, must be after the start date and should ideally be before or on the deadline.
     * </p>
     */
    private LocalDate endDate;
    /**
     * The current status of the project.
     * <p>
     * Represents the project's current state in its lifecycle. Must be one of the
     * predefined statuses in {@link ProjectStatus}.
     * </p>
     */
    @NotNull(message = "Status is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @Min(value = 1, message = "Priority must be between 1 and 5")
    @Max(value = 5, message = "Priority must be between 1 and 5")
    @Column(nullable = false)
    private Integer priority = 3;

    @Column(nullable = false)
    private boolean isTemplate = false;

    @Size(max = 2000)
    private String acceptanceCriteria;

    private String repositoryUrl;

    /**
     * The team responsible for this project.
     * <p>
     * Each project must be assigned to exactly one team. The team is responsible
     * for completing all tasks and components within the project by the deadline.
     * </p>
     */
    @NotNull(message = "Team is mandatory")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Team team;
    /**
     * The collection of tasks that make up this project.
     * <p>
     * Tasks represent individual units of work within the project. They are managed
     * with cascade operations, meaning that deleting a project will delete all its tasks.
     * </p>
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(name, project.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}