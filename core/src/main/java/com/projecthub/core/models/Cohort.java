package com.projecthub.core.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

/**
 * Represents a cohort within a {@link School}.
 * <p>
 * Cohorts contain multiple teams.
 * </p>
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        indexes = {
                @Index(name = "idx_cohort_name", columnList = "name")
        }
)
@Getter
@Setter
@ToString
public class Cohort extends BaseEntity {

    /**
     * The name of the cohort.
     */
    @NotBlank(message = "Cohort name is mandatory")
    @Size(max = 100, message = "Cohort name must be less than 100 characters")
    @Column(unique = true, nullable = false)
    private String name;

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

    /**
     * Default constructor required by JPA.
     */
    public Cohort() {
    }

    /**
     * Constructs a new cohort with the specified fields.
     *
     * @param name   the name of the cohort
     * @param school the school to which the cohort belongs
     */
    public Cohort(String name, School school) {
        this.name = name;
        this.school = school;
    }
}