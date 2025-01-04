package com.projecthub.base.project.api.command;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public final class CreateProjectCommand extends ProjectCommand {
    @NotBlank
    private final String name;
    @Size(max = 500)
    private final String description;
    @Future
    private final LocalDate deadline;
    @NotNull
    private final UUID teamId;
    @Min(1)
    @Max(5)
    private final Integer priority;
    private final boolean isTemplate;
    private final String acceptanceCriteria;
    private final String repositoryUrl;


    public CreateProjectCommand(UUID initiatorId, String name, String description,
                                LocalDate deadline, UUID teamId, Integer priority, boolean isTemplate,
                                String acceptanceCriteria, String repositoryUrl) {
        super(initiatorId);
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.teamId = teamId;
        this.priority = priority;
        this.isTemplate = isTemplate;
        this.acceptanceCriteria = acceptanceCriteria;
        this.repositoryUrl = repositoryUrl;
    }

    // Getters for all fields
}
