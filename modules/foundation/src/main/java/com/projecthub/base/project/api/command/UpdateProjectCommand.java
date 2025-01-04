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


    public UpdateProjectCommand(UUID initiatorId, UUID projectId, String name,
                                String description, LocalDate deadline, Integer priority,
                                String acceptanceCriteria, String repositoryUrl) {
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
