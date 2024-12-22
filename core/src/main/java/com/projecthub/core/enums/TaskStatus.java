package com.projecthub.core.enums;

public enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    FAILED;

    public static boolean isValidStatus(String status) {
        for (TaskStatus ts : TaskStatus.values()) {
            if (ts.name().equals(status)) {
                return true;
            }
        }
        return false;
    }
}