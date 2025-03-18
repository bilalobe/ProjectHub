package com.projecthub.domain.project

/**
 * Represents the various states a project can be in throughout its lifecycle.
 * Used for workflow state management of projects.
 */
enum class ProjectStatus {
    /**
     * Initial state of a project when it's first created
     */
    DRAFT,

    /**
     * Project is in the planning phase, with team assignment and task definition
     */
    PLANNING,

    /**
     * Active development work is being done on the project
     */
    IN_PROGRESS,

    /**
     * Project tasks are complete and awaiting final review
     */
    REVIEW,

    /**
     * Project has been successfully completed
     */
    COMPLETED,

    /**
     * Project has been abandoned or stopped before completion
     */
    CANCELLED,

    /**
     * Project is temporarily on hold
     */
    ON_HOLD;

    /**
     * Determines if this status represents an active project.
     * @return true if the project is currently active
     */
    val isActive: Boolean
        get() = this == IN_PROGRESS || this == PLANNING || this == REVIEW

    /**
     * Determines if this status represents a terminal state.
     * @return true if the project is in a terminal state
     */
    val isTerminal: Boolean
        get() = this == COMPLETED || this == CANCELLED

    /**
     * Determines if the project can be edited in this state.
     * @return true if the project can be edited
     */
    val isEditable: Boolean
        get() = this != COMPLETED && this != CANCELLED
}
