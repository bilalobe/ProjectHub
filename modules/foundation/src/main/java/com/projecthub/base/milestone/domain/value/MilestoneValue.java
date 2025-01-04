package com.projecthub.base.milestone.domain.value;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MilestoneValue(
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100)
    String name,

    @Size(max = 500)
    String description,

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    LocalDate dueDate
) {
}
