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
@Table(
    indexes = {
        @Index(name = "idx_cohort_name", columnList = "name"),
        @Index(name = "idx_cohort_school_name", columnList = "school_id, name")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_school_name", columnNames = {"school_id", "name"})
    }
)
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
    private boolean isArchived = false;

    @Column(nullable = false)
    private boolean isActive = true;

    @NotNull
    @Embedded
    private CohortAssignment assignment;

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

    public void assignToSchool(School school, SchoolYear year, GradeLevel level, int maxStudents) {
        this.school = school;
        this.assignment = CohortAssignment.create(
            getId(),
            year,
            level,
            maxStudents,
            teams.size()
        );
    }

    public boolean isCompleted() {
        return !isActive && !isArchived;
    }

    public void addTeam(Team team, CohortValidator validator) {
        validator.validateAddTeam(this, team);
        team.setCohort(this);
        teams.add(team);
    }

    public void removeTeam(Team team, CohortValidator validator) {
        validator.validateRemoveTeam(this, team);
        teams.remove(team);
        team.setCohort(null);
    }

    public void markAsCompleted(CohortValidator validator) {
        validator.validateMarkAsCompleted(this);
        this.isActive = false;
    }

    public void archive(String reason, CohortValidator validator) {
        validator.validateArchive(this, reason);
        this.isArchived = true;
        this.isActive = false;
    }

    public void updateDetails(LocalDate startTerm, LocalDate endTerm, CohortValidator validator) {
        validator.validateUpdateDetails(this, startTerm, endTerm);
        this.startTerm = startTerm;
        this.endTerm = endTerm;
    }

    public void updateMaxStudents(int maxStudents, CohortValidator validator) {
        validator.validateUpdateMaxStudents(this, maxStudents);
        this.assignment = CohortAssignment.create(
            getId(),
            assignment.year(),
            assignment.level(),
            maxStudents,
            teams.size()
        );
    }
}
