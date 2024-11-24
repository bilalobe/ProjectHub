package com.projecthub.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/**
 * Represents a team within a cohort.
 * Teams can have multiple users and projects.
 */
@Entity
public class Team {

    /**
     * The unique identifier for the team.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the team.
     */
    private String name;

    /**
     * The cohort to which the team belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cohort_id", nullable = false)
    private Cohort cohort;

    /**
     * The school to which the team belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    /**
     * The list of projects associated with the team.
     */
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects;

    /**
     * The list of users in the team.
     */
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppUser> members;

    // Default constructor required by JPA
    public Team() {
    }

    // Constructor with all fields
    public Team(Long id) {
        this.id = id;
    }

    // Constructor without id (for new Teams)
    public Team(String name, School school) {
        this.name = name;
        this.school = school;
    }

    // Constructor with cohort
    public Team(String name, Cohort cohort) {
        this.name = name;
        this.cohort = cohort;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public School getSchool() {
        return school;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<AppUser> getMembers() {
        return members;
    }

    public Cohort getCohort() {
        return cohort;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public void setMembers(List<AppUser> members) {
        this.members = members;
    }

    public void setCohort(Cohort cohort) {
        this.cohort = cohort;
    }

    // Override toString method
    @Override
    public String toString() {
        return "Team{id=" + id + ", name='" + name + "', school=" + school + "}";
    }
}