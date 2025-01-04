package com.projecthub.base.school.cohort.domain.command;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class CompleteCohortCommand {
    @NotNull(message = "Cohort ID is required")
    UUID id;
}
