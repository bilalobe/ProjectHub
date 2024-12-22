package com.projecthub.core.models;

import com.projecthub.core.entities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Represents a cohort within a {@link School}.
 * <p>
 * A cohort is a group of teams that progress through the curriculum together.
 * Cohorts are used to organize teams within a school and typically represent
 * a specific time period or academic term.
 * </p>
 * <p>
 * Business rules:
 * <ul>
 *   <li>Each cohort must have a unique name within its school</li>
 *   <li>Cohorts must belong to exactly one school</li>
 *   <li>Cohorts can contain multiple teams</li>
 *   <li>Cohort names are limited to 100 characters</li>
 *   <li>Deleting a cohort will cascade to its teams</li>
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
public class Cohort extends BaseEntity {

    /**
     * The name of the cohort.
     * <p>
     * The name must be unique within a school and serves as a business identifier
     * for the cohort. Examples might include "Spring 2024" or "Fall Batch 2023".
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cohort cohort = (Cohort) o;
        return Objects.equals(name, cohort.name) &&
                Objects.equals(school, cohort.school);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, school);
    }
}