package com.projecthub.base.project.api.command;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public final class UpdateProjectCommand extends ProjectCommand {
    @NotNull
    private final UUID projectId;
    private final String name;
    private final String description;
    private final LocalDate deadline;
    private final Integer priority;
    private final String acceptanceCriteria;
    private final String repositoryUrl;


    public UpdateProjectCommand(final UUID initiatorId, final UUID projectId, final String name,
                                final String description, final LocalDate deadline, final Integer priority,
                                final String acceptanceCriteria, final String repositoryUrl) {
        super(initiatorId);
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.acceptanceCriteria = acceptanceCriteria;
        this.repositoryUrl = repositoryUrl;
    }
}
