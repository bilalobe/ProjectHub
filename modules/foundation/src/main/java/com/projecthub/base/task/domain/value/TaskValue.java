package com.projecthub.base.task.domain.value;

import com.projecthub.base.task.domain.enums.TaskStatus;
import com.projecthub.base.user.domain.entity.AppUser;

import java.time.LocalDate;

public record TaskValue(
    String name,
    TaskStatus status,
    LocalDate plannedStartDate,
    LocalDate dueDate,
    AppUser assignee,
    Integer progress
) {
    public boolean isStarted() {
        return status == TaskStatus.STARTED;
    }
}
