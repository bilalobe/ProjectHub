package com.projecthub.dto;

import java.util.Optional;

import com.projecthub.model.Student;

/**
 * Data Transfer Object for Student summary information.
 */
public class StudentSummary {

    private final Long id;
    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String teamName;

    // Constructor
    public StudentSummary(Long id, String username, String email, String firstName, String lastName, String teamName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.teamName = teamName;
    }

    /**
     * Constructs a StudentSummary from a Student entity.
     *
     * @param student the Student entity
     */
    public StudentSummary(Student student) {
        this.id = student.getId();
        this.username = student.getUsername();
        this.email = student.getEmail();
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        this.teamName = student.getTeam() != null ? student.getTeam().getName() : null;
    }

    /**
     * Constructs a StudentSummary from a Student entity.
     *
     * @param student the Student entity
     */
    public StudentSummary(Optional<Student> studentOptional) {
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            this.id = student.getId();
            this.username = student.getUsername();
            this.email = student.getEmail();
            this.firstName = student.getFirstName();
            this.lastName = student.getLastName();
            this.teamName = student.getTeam() != null ? student.getTeam().getName() : null;
        } else {
            this.id = null;
            this.username = null;
            this.email = null;
            this.firstName = null;
            this.lastName = null;
            this.teamName = null;
        }
    }

    // Getters and Setters
    // ...
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTeamName() {
        return teamName;
    }
}