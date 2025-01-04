package com.projecthub.base.cohort.domain.command;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class AssignTeamToCohortCommand {
    @NotNull(message = "Team ID is required")
    UUID teamId;

    @NotNull(message = "Cohort ID is required")
    UUID cohortId;
}
