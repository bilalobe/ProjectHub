package com.projecthub.base.milestone.api.graphql.input;

import com.projecthub.base.milestone.domain.enums.MilestoneStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateMilestoneInput {

    @NotBlank(message = "Milestone name is required")
    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @NotNull(message = "Project ID is required")
    private UUID projectId;
}
