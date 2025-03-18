package com.projecthub.base.project.domain.value;

import java.time.LocalDate;

public record ProjectProgress(
    LocalDate startDate,
    LocalDate endDate,
    int completedTasks,
    int totalTasks
) {
}
