package com.projecthub.core.enums;

/**
 * Represents the various statuses a project can have within the system.
 */
public enum ProjectStatus {
    /** The project has not been started yet */
    NOT_STARTED,
    /** The project is currently in progress */
    IN_PROGRESS,
    /** The project has been completed successfully */
    COMPLETED,
    /** The project is on hold temporarily */
    ON_HOLD,
    /** The project has been cancelled */
    CANCELLED
}