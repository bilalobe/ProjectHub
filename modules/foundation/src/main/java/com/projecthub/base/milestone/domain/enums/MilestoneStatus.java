package com.projecthub.base.milestone.domain.enums;

/**
 * Represents the various statuses that a milestone can have within the project.
 *
 * <p>
 * The possible statuses are:
 * <ul>
 *   <li>PENDING: Awaiting Start</li>
 *   <li>IN_PROGRESS: In Progress</li>
 *   <li>BLOCKED: Blocked</li>
 *   <li>COMPLETED: Completed</li>
 *   <li>CANCELLED: Cancelled</li>
 * </ul>
 * </p>
 * <p>
 * This enum provides utility methods to determine if the status is terminal or active.
 */
public enum MilestoneStatus {
    PENDING("Awaiting Start"),
    IN_PROGRESS("In Progress"),
    BLOCKED("Blocked"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    MilestoneStatus(final String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isTerminal() {
        return COMPLETED == this || CANCELLED == this;
    }

    public boolean isActive() {
        return IN_PROGRESS == this || PENDING == this;
    }
}
