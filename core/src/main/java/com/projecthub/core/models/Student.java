package com.projecthub.core.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents a student in the system.
 * <p>
 * A student can have multiple submissions and belongs to a {@link Team}.
 * </p>
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        indexes = {
                @Index(name = "idx_student_email", columnList = "email")
        }
)
@Getter
@Setter
public class Student extends BaseEntity {

    /**
     * The student's email address.
     */
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * The student's first name.
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

    // Default constructor required by JPA
    public Student() {
    }

    /**
     * Constructor with all fields.
     */
    public Student(String email, String firstName, String lastName, Team team) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.team = team;
    }
}