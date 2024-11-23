package com.projecthub.dto;

import com.projecthub.model.Team;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for the Team entity.
 * Used for transferring team data between processes.
 */
public class TeamSummary {
    private final Long id;

    @NotBlank(message = "Team name is mandatory")
    private final String name;

    /**
     * Default constructor.
     */
    public TeamSummary() {
        this.id = null;
        this.name = null;
    }

    /**
     * Constructs a TeamSummary from a Team entity.
     *
     * @param team the Team entity
     */
    public TeamSummary(Team team) {
        this.id = team.getId();
        this.name = team.getName();
    }

    /**
     * Gets the team's ID.
     *
     * @return the team's ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the team's name.
     *
     * @return the team's name
     */
    public String getName() {
        return name;
    }
}