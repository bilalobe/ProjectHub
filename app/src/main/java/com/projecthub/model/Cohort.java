package com.projecthub.model;

import java.time.LocalDateTime;
import java.util.List;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
public class Cohort {

    /**
     * The unique identifier for the cohort.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The timestamp when the cohort was created.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the cohort was last updated.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * The user who created the cohort.
     */
    @CreatedBy
    private String createdBy;

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
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    /**
     * The list of teams in the cohort.
     */
    @OneToMany(mappedBy = "cohort", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams;

    private boolean deleted = false;

    public Cohort() {}

    public Cohort(UUID id, String name, School school, List<Team> teams) {
        this.id = id;
        this.name = name;
        this.school = school;
        this.teams = teams;
    }

    public Cohort(String name, School school) {
        this.name = name;
        this.school = school;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public School getSchool() {
        return school;
    }

    public List<Team> getTeams() {
        return teams;
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

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}