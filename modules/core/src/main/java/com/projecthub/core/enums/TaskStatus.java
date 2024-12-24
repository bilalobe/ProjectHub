package com.projecthub.core.enums;

/**
 * Represents the current status of a task in the system.
 */
public enum TaskStatus {
    /** Task is waiting to be started */
    PENDING,
    /** Task is currently being worked on */
    IN_PROGRESS,
    /** Task has been successfully completed */
    COMPLETED,
    /** Task has been cancelled before completion */
    CANCELLED,
    /** Task could not be completed due to issues */
    FAILED;

    /**
     * Validates if a given status string matches any defined TaskStatus.
     *
     * @param status The status string to validate
     * @return true if the status exists in the enum, false otherwise
     */
    public static boolean isValidStatus(String status) {
        for (TaskStatus ts : TaskStatus.values()) {
            if (ts.name().equals(status)) {
                return true;
            }
        }
        return false;
    }
}