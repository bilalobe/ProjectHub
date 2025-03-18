package com.projecthub.base.cohort.domain.entity;

import com.projecthub.base.cohort.domain.enums.GradeLevel;
import com.projecthub.base.cohort.domain.validation.CohortValidator;
import com.projecthub.base.cohort.domain.value.CohortAssignment;
import com.projecthub.base.cohort.domain.value.SchoolYear;
import com.projecthub.base.school.domain.entity.School;
import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.team.domain.entity.Team;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a cohort within a {@link School}.
 * <p>
 * A cohort is a group of teams that progress through the curriculum together.
 * Cohorts are used to organize teams within a school and typically represent a
 * specific time period or academic term.
 * </p>
 * <p>
 * Business rules:
 * <ul>
 * <li>Each cohort must have a unique name within its school</li>
 * <li>Cohorts must belong to exactly one school</li>
 * <li>Cohorts can contain multiple teams</li>
 * <li>Cohort names are limited to 100 characters</li>
 * <li>Deleting a cohort will cascade to its teams</li>
 * </ul>
 * </p>
 *
 * @see School
 * @see Team
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(indexes = {
    @Index(name = "idx_cohort_name", columnList = "name"),
    @Index(name = "idx_cohort_school_name", columnList = "school_id, name")
}, uniqueConstraints = @UniqueConstraint(name = "uc_school_name", columnNames = {"school_id", "name"}))
@Getter
@Setter
@ToString(exclude = {"teams", "school"})
@EqualsAndHashCode(of = {"name", "school"}, callSuper = false)
public class Cohort extends BaseEntity {

    /**
     * The name of the cohort.
     * <p>
     * The name must be unique within a school and serves as a business
     * identifier for the cohort. Examples might include "Spring 2024" or "Fall
     * Batch 2023".
     * </p>
     */
    @NotBlank(message = "Cohort name is mandatory")
    @Size(max = 100, message = "Cohort name must be less than 100 characters")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Start term is mandatory")
    @Column(nullable = false)
    private LocalDate startTerm;

    @NotNull(message = "End term is mandatory")
    @Column(nullable = false)
    private LocalDate endTerm;

    @Column(nullable = false)
    private boolean isArchived;

    @Column(nullable = false)
    private boolean isActive = true;

    @NotNull
    @Embedded
    private CohortAssignment assignment;

    @Embedded
    private SeatingConfiguration seatingConfig;

    /**
     * The school to which this cohort belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private School school;

    /**
     * The list of teams in the cohort.
     */
    @OneToMany(mappedBy = "cohort", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams;

    public Cohort() {
    }

    public void assignToSchool(final School school, final SchoolYear year, final GradeLevel level, final int maxStudents) {
        this.school = school;
        assignment = CohortAssignment.create(
            this.getId(),
            year,
            level,
            maxStudents,
            this.teams.size()
        );
    }

    public boolean isCompleted() {
        return !this.isActive && !this.isArchived;
    }

    public void addTeam(final Team team, final CohortValidator validator) {
        CohortValidator.validateAddTeam(this, team);
        team.setCohort(this);
        this.teams.add(team);
    }

    public void removeTeam(final Team team, final CohortValidator validator) {
        CohortValidator.validateRemoveTeam(this, team);
        this.teams.remove(team);
        team.setCohort(null);
    }

    public void markAsCompleted(final CohortValidator validator) {
        CohortValidator.validateMarkAsCompleted(this);
        isActive = false;
    }

    public void archive(final String reason, final CohortValidator validator) {
        CohortValidator.validateArchive(this, reason);
        isArchived = true;
        isActive = false;
    }

    public void updateDetails(final LocalDate startTerm, final LocalDate endTerm, final CohortValidator validator) {
        CohortValidator.validateUpdateDetails(this, startTerm, endTerm);
        this.startTerm = startTerm;
        this.endTerm = endTerm;
    }

    public void updateMaxStudents(final int maxStudents, final CohortValidator validator) {
        CohortValidator.validateUpdateMaxStudents(this, maxStudents);
        assignment = CohortAssignment.create(
            this.getId(),
            this.assignment.year(),
            this.assignment.level(),
            maxStudents,
            this.teams.size()
        );
    }

    public void configureSeating(int rows, int columns, String layoutType, CohortValidator validator) {
        validator.validateSeatingConfiguration(this, rows, columns);
        this.seatingConfig = SeatingConfiguration.create(rows, columns, layoutType);
    }
}
