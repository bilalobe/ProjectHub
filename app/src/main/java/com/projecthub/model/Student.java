package com.projecthub.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
public class Student {

    /**
     * The unique identifier for the student.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The timestamp when the student was created.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the student was last updated.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * The user who created the student.
     */
    @CreatedBy
    private String createdBy;

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
    @JoinColumn(name = "team_id", nullable = false)
    @NotNull(message = "Team is mandatory")
    private Team team;

    private boolean deleted = false;

    // Default constructor required by JPA
    public Student() {
    }

    // Constructor with all fields
    public Student(UUID id, String email, String firstName, String lastName, Team team) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.team = team;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", email='" + email + "', firstName='" + firstName + "', lastName='" + lastName + "', team=" + team + "}";
    }
}