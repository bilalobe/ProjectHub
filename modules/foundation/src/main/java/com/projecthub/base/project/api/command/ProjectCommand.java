package com.projecthub.base.project.api.command;

import java.time.Instant;
import java.util.UUID;

public abstract class ProjectCommand {
    private final UUID commandId;
    private final Instant timestamp;
    private final UUID initiatorId;

    protected ProjectCommand(UUID initiatorId) {
        this.commandId = UUID.randomUUID();
        this.timestamp = Instant.now();
        this.initiatorId = initiatorId;
    }

    public UUID getCommandId() {
        return commandId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public UUID getInitiatorId() {
        return initiatorId;
    }
}
