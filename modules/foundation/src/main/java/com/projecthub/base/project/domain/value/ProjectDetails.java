package com.projecthub.base.project.domain.value;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ProjectDetails(
    @NotBlank(message = "Description is mandatory")
    @Size(max = 500)
    String description,

    @NotNull(message = "Deadline is mandatory")
    @Future(message = "Deadline must be in the future")
    LocalDate deadline,

    @Min(1) @Max(5)
    Integer priority,

    @Size(max = 2000)
    String acceptanceCriteria,

    String repositoryUrl
) {
}
