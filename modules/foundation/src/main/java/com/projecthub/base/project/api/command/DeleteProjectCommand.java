package com.projecthub.base.project.api.command;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public final class DeleteProjectCommand extends ProjectCommand {
    @NotNull
    private final UUID projectId;
    private final String reason;

    public DeleteProjectCommand(final UUID initiatorId, final UUID projectId, final String reason) {
        super(initiatorId);
        this.projectId = projectId;
        this.reason = reason;
    }
}
