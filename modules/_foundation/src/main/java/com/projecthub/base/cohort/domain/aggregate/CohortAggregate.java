package com.projecthub.base.cohort.domain.aggregate;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate root for the Cohort domain.
 * <p>
 * A cohort is a group of teams that progress through the curriculum together.
 * This aggregate enforces consistency rules and business invariants for the cohort domain.
 * </p>
 */
@Entity
@Table(name = "cohorts", indexes = {
    @Index(name = "idx_cohort_name", columnList = "name"),
    @Index(name = "idx_cohort_school_name", columnList = "school_id, name")
}, uniqueConstraints = @UniqueConstraint(name = "uc_school_name", columnNames = {"school_id", "name"}))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString(exclude = {"teams", "school"})
@EqualsAndHashCode(of = {"name", "school"}, callSuper = false)
public class CohortAggregate extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private LocalDate startTerm;

    @NotNull
    @Column(nullable = false)
    private LocalDate endTerm;

    @Column(nullable = false)
    private boolean isArchived;

    @Column(nullable = false)
    private boolean isActive = true;

    @NotNull
    @Embedded
    private CohortAssignment assignment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @OneToMany(
        mappedBy = "cohort",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<Team> teams = new ArrayList<>();

    public CohortAggregate() {
    }

    public static CohortAggregate create(
        final String name,
        final LocalDate startTerm,
        final LocalDate endTerm,
        final School school,
        final SchoolYear year,
        final GradeLevel level,
        final int maxStudents,
        final CohortValidator validator
    ) {
        final CohortAggregate cohort = new CohortAggregate();
        cohort.name = name;
        cohort.startTerm = startTerm;
        cohort.endTerm = endTerm;
        cohort.school = school;
        cohort.isActive = true;
        cohort.isArchived = false;

        cohort.assignment = CohortAssignment.create(
            cohort.getId(),
            year,
            level,
            maxStudents,
            0
        );

        validator.validateCreate(cohort, school);
        return cohort;
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

    public void archive(final String reason, final CohortValidator validator) {
        CohortValidator.validateArchive(this, reason);
        isArchived = true;
        isActive = false;
    }

    public void markAsCompleted(final CohortValidator validator) {
        CohortValidator.validateMarkAsCompleted(this);
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

    public boolean isCompleted() {
        return !this.isActive && !this.isArchived;
    }
}
