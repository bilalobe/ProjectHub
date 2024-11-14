package com.projecthub.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // One-to-Many relationship with Team
    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Team> teams;

    // Default constructor required by JPA
    public School() {
    }

    // Constructor with all fields
    public School(Long id, String name, List<Team> teams) {
        this.id = id;
        this.name = name;
        this.teams = teams;
    }

    // Constructor without id (for new Schools)
    public School(String name) {
        this.name = name;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    // Override toString method
    @Override
    public String toString() {
        return "School{id=" + id + ", name='" + name + "'}";
    }
}