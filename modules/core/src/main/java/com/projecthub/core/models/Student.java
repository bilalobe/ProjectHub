package com.projecthub.core.models;

import com.projecthub.core.entities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a student in the ProjectHub system.
 * <p>
 * Students are individuals enrolled in courses and assigned to teams. Each student
 * has a unique email address and can submit work for projects. Students are organized
 * into teams and can make multiple submissions.
 * </p>
 * <p>
 * Business rules:
 * <ul>
 *   <li>Each student must have a unique email address</li>
 *   <li>Students must belong to exactly one team</li>
 *   <li>Student names must not exceed specified lengths</li>
 *   <li>Email addresses must be valid and institutional</li>
 * </ul>
 * </p>
 *
 * @see Team
 * @see Submission
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        indexes = {
                @Index(name = "idx_student_email", columnList = "email"),
                @Index(name = "idx_student_last_name", columnList = "lastName"),
                @Index(name = "idx_student_team", columnList = "team_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_student_email", columnNames = "email")
        }
)
@Getter
@Setter
@ToString(exclude = "team")
public class Student extends BaseEntity {

    /**
     * The student's email address.
     * <p>
     * Serves as the unique identifier for the student within the system.
     * Must be a valid email format and is used for system communications.
     * </p>
     */
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    @Column(unique = true, nullable = false)
    private String email;

    @Size(max = 50)
    private String middleName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    private String phoneNumber;

    @NotNull
    @Column(nullable = false)
    private LocalDate enrollmentDate;

    private String studentId;

    private String emergencyContact;

    /**
     * The student's first name.
     * <p>
     * Used for display and identification purposes throughout the system.
     * Must not exceed 50 characters.
     * </p>
     */
    @NotBlank(message = "First name is mandatory")
    @Size(max = 50, message = "First name must be less than 50 characters")
    @Column(nullable = false)
    private String firstName;

    /**
     * The student's last name.
     */
    @NotBlank(message = "Last name is mandatory")
    @Size(max = 50, message = "Last name must be less than 50 characters")
    @Column(nullable = false)
    private String lastName;
    /**
     * The team that the student belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull(message = "Team is mandatory")
    private Team team;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Student student = (Student) o;
        return email.equals(student.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

}