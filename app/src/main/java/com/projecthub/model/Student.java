package com.projecthub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Represents a student in the system.
 * A student can have multiple submissions and belongs to a team.
 */
@Entity
public class Student {

    /**
     * The unique identifier for the student.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The student's username.
     */
    private String username;

    /**
     * The student's email.
     */
    private String email;

    /**
     * The student's first name.
     */
    private String firstName;

    /**
     * The student's last name.
     */
    private String lastName;

    /**
     * The team that the student belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    /**
     * Default constructor required by JPA.
     */
    public Student() {
    }

    /**
     * Constructs a new student with the specified ID, username, email, first name, last name, and team.
     *
     * @param id        the student's ID
     * @param username  the student's username
     * @param email     the student's email
     * @param firstName the student's first name
     * @param lastName  the student's last name
     * @param team      the team the student belongs to
     */
    public Student(Long id, String username, String email, String firstName, String lastName, Team team) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.team = team;
    }

    // Getters and Setters

    /**
     * Returns the student's ID.
     *
     * @return the student's ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the student's ID.
     *
     * @param id the student's ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the student's username.
     *
     * @return the student's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the student's username.
     *
     * @param username the student's username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the student's email.
     *
     * @return the student's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the student's email.
     *
     * @param email the student's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the student's first name.
     *
     * @return the student's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the student's first name.
     *
     * @param firstName the student's first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the student's last name.
     *
     * @return the student's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the student's last name.
     *
     * @param lastName the student's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the team that the student belongs to.
     *
     * @return the team
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Sets the team that the student belongs to.
     *
     * @param team the team
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Returns a string representation of the student.
     *
     * @return a string containing the student's ID and username
     */
    @Override
    public String toString() {
        return "Student{id=" + id + ", username='" + username + "', email='" + email + "', firstName='" + firstName + "', lastName='" + lastName + "', team=" + team + "}";
    }
}