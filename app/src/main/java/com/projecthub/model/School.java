package com.projecthub.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Represents a school in the system.
 * A school contains multiple cohorts.
 */
@Entity
public class School {

    /**
     * The unique identifier for the school.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the school.
     */
    private String name;

    /**
     * The address of the school.
     */
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

    // Default constructor required by JPA
    public School() {
    }

    // Constructor with all fields
    public School(Long id, String name, String address, List<Team> teams) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.teams = teams;
    }

    // Constructor without id (for new Schools)
    public School(String name, String address) {
        this.name = name;
        this.address = address;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<Cohort> getCohorts() {
        return cohorts;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public void setCohorts(List<Cohort> cohorts) {
        this.cohorts = cohorts;
    }

    // Override toString method
    @Override
    public String toString() {
        return "School{id=" + id + ", name='" + name + "', address='" + address + "'}";
    }
}