package com.projecthub.base.project.api.command;

import java.time.Instant;
import java.util.UUID;

public abstract class ProjectCommand {
    private final UUID commandId;
    private final Instant timestamp;
    private final UUID initiatorId;

    protected ProjectCommand(final UUID initiatorId) {
        commandId = UUID.randomUUID();
        timestamp = Instant.now();
        this.initiatorId = initiatorId;
    }

    public UUID getCommandId() {
        return this.commandId;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public UUID getInitiatorId() {
        return this.initiatorId;
    }
}
