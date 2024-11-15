package com.projecthub.dto;

import com.projecthub.model.Team;

public class TeamSummary {
    private Long id;
    private String name;

    public TeamSummary() {}

    public TeamSummary(Team team) {
        this.id = team.getId();
        this.name = team.getName();
    }

    // Getters and setters
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
}