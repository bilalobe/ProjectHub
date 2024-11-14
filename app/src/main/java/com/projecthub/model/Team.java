package com.projecthub.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Represents a team within a school.
 * Teams can work on multiple projects and contain multiple students.
 */
@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Many-to-One relationship with School
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "school_id")
    private School school;

    // One-to-Many relationship with Project
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Project> projects;

    // Default constructor required by JPA
    public Team() {
    }

    // Constructor with all fields
    public Team(Long id, String name, School school, List<Project> projects) {
        this.id = id;
        this.name = name;
        this.school = school;
        this.projects = projects;
    }

    // Constructor without id (for new Teams)
    public Team(String name, School school) {
        this.name = name;
        this.school = school;
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

    // Override toString method
    @Override
    public String toString() {
        return "Team{id=" + id + ", name='" + name + "', school=" + school + "}";
    }
}