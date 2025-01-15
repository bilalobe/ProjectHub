package com.projecthub.base.milestone.domain.command;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public record DeleteMilestoneCommand(
    @NotNull(message = "Milestone id is required")
    UUID id,
    UUID initiatorId
) {
    public DeleteMilestoneCommand {
        Objects.requireNonNull(id, "Milestone id cannot be null");
        Objects.requireNonNull(initiatorId, "Initiator id cannot be null");
    }

    public static DeleteMilestoneCommandBuilder builder() {
        return new DeleteMilestoneCommandBuilder();
    }

    public static class DeleteMilestoneCommandBuilder {
        private UUID id;
        private UUID initiatorId;

        public DeleteMilestoneCommandBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        public DeleteMilestoneCommandBuilder initiatorId(final UUID initiatorId) {
            this.initiatorId = initiatorId;
            return this;
        }

        public DeleteMilestoneCommand build() {
            return new DeleteMilestoneCommand(this.id, this.initiatorId);
        }
    }
}
