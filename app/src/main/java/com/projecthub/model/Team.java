package com.projecthub.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
public class Team {

    /**
     * The unique identifier for the team.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The timestamp when the team was created.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the team was last updated.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * The user who created the team.
     */
    @CreatedBy
    private String createdBy;

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
    @JoinColumn(name = "school_id", nullable = false)
    @NotNull(message = "School is mandatory")
    private School school;

    /**
     * The cohort to which this team belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cohort_id", nullable = false)
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

    private boolean deleted = false;

    // Default constructor required by JPA
    public Team() {
    }

    // Constructor with all fields
    public Team(UUID id, String name, School school, Cohort cohort, List<Student> students, Set<AppUser> members) {
        this.id = id;
        this.name = name;
        this.school = school;
        this.cohort = cohort;
        this.students = students;
        this.members = members;
    }

    // Constructor without id (for new Teams)
    public Team(String name, School school, Cohort cohort) {
        this.name = name;
        this.school = school;
        this.cohort = cohort;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Cohort getCohort() {
        return cohort;
    }

    public void setCohort(Cohort cohort) {
        this.cohort = cohort;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Set<AppUser> getMembers() {
        return members;
    }

    public void setMembers(Set<AppUser> members) {
        this.members = members;
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
        return "Team{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", school=" + (school != null ? school.getName() : "null")
                + ", cohort=" + (cohort != null ? cohort.getName() : "null")
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + ", createdBy='" + createdBy + '\''
                + ", deleted=" + deleted
                + '}';
    }
}