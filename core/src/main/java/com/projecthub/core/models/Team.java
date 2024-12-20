package com.projecthub.core.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a team within a cohort.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        indexes = {
                @Index(name = "idx_team_name", columnList = "name")
        }
)
@Getter
@Setter
@ToString
public class Team extends BaseEntity {

    /**
     * The name of the team.
     */
    @NotBlank(message = "Team name is mandatory")
    @Size(max = 100, message = "Team name must be less than 100 characters")
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * The school to which this team belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull(message = "School is mandatory")
    private School school;

    /**
     * The cohort to which this team belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull(message = "Cohort is mandatory")
    private Cohort cohort;

    /**
     * The list of students in the team.
     */
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Student> students;

    /**
     * The members of the team.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<AppUser> members;

    /**
     * Default constructor required by JPA.
     */
    public Team() {
    }

    /**
     * Constructs a new team with the specified fields.
     */
    public Team(UUID id, String name, School school, Cohort cohort, List<Student> students, Set<AppUser> members) {
        this.setId(id);
        this.name = name;
        this.school = school;
        this.cohort = cohort;
        this.students = students;
        this.members = members;
    }

    /**
     * Constructs a new team with the specified fields, excluding the ID.
     */
    public Team(String name, School school, Cohort cohort) {
        this.name = name;
        this.school = school;
        this.cohort = cohort;
    }
}