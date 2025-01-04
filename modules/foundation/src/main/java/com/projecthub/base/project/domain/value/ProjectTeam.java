package com.projecthub.base.project.domain.value;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProjectTeam(
    @NotNull(message = "Team ID is required")
    UUID teamId,

    @Min(value = 1, message = "Maximum capacity must be at least 1")
    int capacity,

    @Min(value = 0, message = "Current capacity cannot be negative")
    int currentCapacity
) {
    public ProjectTeam {
        if (currentCapacity > capacity) {
            throw new IllegalArgumentException("Current capacity cannot exceed maximum capacity");
        }
    }

    public boolean hasCapacity() {
        return currentCapacity < capacity;
    }

    public int remainingCapacity() {
        return capacity - currentCapacity;
    }

    public boolean isAtCapacity() {
        return currentCapacity == capacity;
    }

    public ProjectTeam withIncreasedCapacity() {
        if (isAtCapacity()) {
            throw new IllegalStateException("Team is at maximum capacity");
        }
        return new ProjectTeam(teamId, capacity, currentCapacity + 1);
    }

    public ProjectTeam withDecreasedCapacity() {
        if (currentCapacity == 0) {
            throw new IllegalStateException("Team is at minimum capacity");
        }
        return new ProjectTeam(teamId, capacity, currentCapacity - 1);
    }
}
