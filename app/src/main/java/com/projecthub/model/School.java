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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a school in the system.
 * <p>
 * A school contains multiple cohorts and teams.
 * </p>
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    indexes = {
        @Index(name = "idx_school_name", columnList = "name")
    }
)
public class School {

    /**
     * The unique identifier for the school.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The name of the school.
     */
    @NotBlank(message = "School name is mandatory")
    @Size(max = 100, message = "School name must be less than 100 characters")
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * The address of the school.
     */
    @NotBlank(message = "Address is mandatory")
    @Size(max = 200, message = "Address must be less than 200 characters")
    @Column(nullable = false)
    private String address;

    /**
     * The list of teams in the school.
     */
    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams;

    /**
     * The list of cohorts in the school.
     */
    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cohort> cohorts;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    private boolean deleted = false;

    // Default constructor required by JPA
    public School() {
    }

    // Constructor with all fields
    public School(UUID id, String name, String address, List<Team> teams, List<Cohort> cohorts, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, boolean deleted) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.teams = teams;
        this.cohorts = cohorts;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.deleted = deleted;
    }

    // Constructor without id (for new Schools)
    public School(String name, String address) {
        this.name = name;
        this.address = address;
    }

    // Getters and setters
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Cohort> getCohorts() {
        return cohorts;
    }

    public void setCohorts(List<Cohort> cohorts) {
        this.cohorts = cohorts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    // Override toString method
    @Override
    public String toString() {
        return "School{id=" + id + ", name='" + name + "', address='" + address + "'}";
    }
}