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
@Table(name = "cohorts",
    indexes = {
        @Index(name = "idx_cohort_name", columnList = "name"),
        @Index(name = "idx_cohort_school_name", columnList = "school_id, name")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_school_name", columnNames = {"school_id", "name"})
    }
)
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
    private boolean isArchived = false;

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

    public static CohortAggregate create(
        String name,
        LocalDate startTerm,
        LocalDate endTerm,
        School school,
        SchoolYear year,
        GradeLevel level,
        int maxStudents,
        CohortValidator validator
    ) {
        CohortAggregate cohort = new CohortAggregate();
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

    public void archive(String reason, CohortValidator validator) {
        validator.validateArchive(this, reason);
        this.isArchived = true;
        this.isActive = false;
    }

    public void markAsCompleted(CohortValidator validator) {
        validator.validateMarkAsCompleted(this);
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

    public boolean isCompleted() {
        return !isActive && !isArchived;
    }
}
