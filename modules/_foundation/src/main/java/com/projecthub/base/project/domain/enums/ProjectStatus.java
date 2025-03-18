package com.projecthub.base.project.domain.enums;

/**
 * Represents the various statuses a project can have within the system.
 */
public enum ProjectStatus {
    CREATED,
    ACTIVE,
    ON_HOLD,
    COMPLETED,
    CANCELLED,
    OVERDUE,
    ARCHIVED,
    REOPENED;

    private final String displayName;

    ProjectStatus(final String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isTerminal() {
        return COMPLETED == this || CANCELLED == this;
    }

    public boolean isActive() {
        return ACTIVE == this;
    }
}
