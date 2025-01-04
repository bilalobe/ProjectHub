package com.projecthub.base.project.domain.enums;

/**
 * Represents the various statuses a project can have within the system.
 */
public enum ProjectStatus {
    DRAFT("In Draft"),
    ACTIVE("Active"),
    ON_HOLD("On Hold"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    IN_PROGRESS("In Progress");

    private final String displayName;

    ProjectStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }
}
