package com.projecthub.dto;

import com.projecthub.model.Student;

/**
 * Data Transfer Object for the Student entity.
 * Used for transferring student data between processes.
 */
public class StudentSummary {
    private Long id;
    private String name;
    private Long teamId;

    public StudentSummary() {}

    public StudentSummary(Long id, String name, Long teamId) {
        this.id = id;
        this.name = name;
        this.teamId = teamId;
    }

    public StudentSummary(Student student) {
        this.id = student.getId();
        this.name = student.getName();
        this.teamId = student.getTeam() != null ? student.getTeam().getId() : null;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
}