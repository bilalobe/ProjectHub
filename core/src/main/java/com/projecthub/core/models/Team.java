package com.projecthub.core.models;

import com.projecthub.core.entities.AppUser;
import com.projecthub.core.entities.BaseEntity;
import com.projecthub.core.enums.TeamStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a team within the ProjectHub system.
 * <p>
 * Teams are groups of students and mentors working together on projects. Each team
 * belongs to a specific cohort within a school. Teams serve as the primary organizational
 * unit for project work and collaboration.
 * </p>
 * <p>
 * Business rules:
 * <ul>
 *   <li>Each team must have a unique name within its school</li>
 *   <li>Teams must belong to exactly one cohort</li>
 *   <li>Teams can have multiple students and project members</li>
 *   <li>Teams can be assigned multiple projects</li>
 * </ul>
 * </p>
 *
 * @see School
 * @see Cohort
 * @see Student
 * @see AppUser
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        indexes = {
                @Index(name = "idx_team_name", columnList = "name"),
                @Index(name = "idx_team_cohort", columnList = "cohort_id"),
                @Index(name = "idx_team_school", columnList = "school_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_team_name_school", columnNames = {"name", "school_id"})
        }
)
@Getter
@Setter
@ToString(exclude = {"school", "cohort", "students", "members"})
public class Team extends BaseEntity {

    /**
     * The name of the team.
     * <p>
     * Serves as a business identifier and must be unique within a school.
     * Maximum length is 100 characters.
     * </p>
     */
    @NotBlank(message = "Team name is mandatory")
    @Size(max = 100, message = "Team name must be less than 100 characters")
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String teamCode;

    @Size(max = 200)
    private String communicationChannel;

    @Size(max = 500)
    private String meetingSchedule;

    @Enumerated(EnumType.STRING)
    private TeamStatus status = TeamStatus.ACTIVE;

    /**
     * The school to which this team belongs.
     * <p>
     * Each team must be associated with exactly one school. This relationship helps
     * organize teams within the broader educational institution.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull(message = "School is mandatory")
    private School school;
    /**
     * The cohort to which this team belongs.
     * <p>
     * Teams are organized into cohorts, which represent groups of teams working
     * during the same time period or academic term.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull(message = "Cohort is mandatory")
    private Cohort cohort;
    /**
     * The students who are members of this team.
     * <p>
     * Represents the collection of students assigned to this team. Student assignments
     * are managed with cascade operations, meaning that removing a team will update
     * or remove the associated student records.
     * </p>
     */
    @Size(min = 1, max = 10, message = "Team must have between 1 and 10 students")
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Student> students;
    /**
     * The team members (mentors, instructors, etc.) associated with this team.
     * <p>
     * Represents the non-student users who are involved with the team, such as
     * mentors, instructors, or other staff members.
     * </p>
     */
    @Size(min = 1, max = 5, message = "Team must have between 1 and 5 members")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<AppUser> members;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(name, team.name) &&
                Objects.equals(cohort, team.cohort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cohort);
    }

}