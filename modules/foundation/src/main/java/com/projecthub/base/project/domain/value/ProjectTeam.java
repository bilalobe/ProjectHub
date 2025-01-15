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
        return this.currentCapacity < this.capacity;
    }

    public int remainingCapacity() {
        return this.capacity - this.currentCapacity;
    }

    public boolean isAtCapacity() {
        return this.currentCapacity == this.capacity;
    }

    public ProjectTeam withIncreasedCapacity() {
        if (this.isAtCapacity()) {
            throw new IllegalStateException("Team is at maximum capacity");
        }
        return new ProjectTeam(this.teamId, this.capacity, this.currentCapacity + 1);
    }

    public ProjectTeam withDecreasedCapacity() {
        if (0 == currentCapacity) {
            throw new IllegalStateException("Team is at minimum capacity");
        }
        return new ProjectTeam(this.teamId, this.capacity, this.currentCapacity - 1);
    }
}
