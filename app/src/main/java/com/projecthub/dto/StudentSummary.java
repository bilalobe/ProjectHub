package com.projecthub.dto;

import java.util.Optional;

import com.projecthub.model.Student;

/**
 * Data Transfer Object for the Student entity.
 * Used for transferring student data between processes.
 */
public class StudentSummary {
    private final Long id;
    private final String name;
    private final Long teamId;

    /**
     * Default constructor.
     */
    public StudentSummary() {
        this.id = null;
        this.name = null;
        this.teamId = null;
    }

    /**
     * Constructs a StudentSummary with specified values.
     *
     * @param id     the student ID
     * @param name   the student's name
     * @param teamId the team ID
     */
    public StudentSummary(Long id, String name, Long teamId) {
        this.id = id;
        this.name = name;
        this.teamId = teamId;
    }

    /**
     * Constructs a StudentSummary from a Student entity.
     *
     * @param student the Student entity
     */
    public StudentSummary(Student student) {
        this.id = student.getId();
        this.name = student.getName();
        this.teamId = student.getTeam() != null ? student.getTeam().getId() : null;
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
            this.name = student.getName();
            this.teamId = student.getTeam() != null ? student.getTeam().getId() : null;
        } else {
            this.id = null;
            this.name = null;
            this.teamId = null;
        }
    }

    /**
     * Gets the student ID.
     *
     * @return the student ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the student's name.
     *
     * @return the student's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the team ID.
     *
     * @return the team ID
     */
    public Long getTeamId() {
        return teamId;
    }
}