package com.projecthub.base.milestone.domain.command;

import com.projecthub.base.milestone.domain.value.MilestoneValue;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public record CreateMilestoneCommand(
    @NotNull(message = "Milestone details are required")
    MilestoneValue milestoneDetails,
    UUID projectId,
    UUID initiatorId
) {
    public CreateMilestoneCommand {
        Objects.requireNonNull(milestoneDetails, "Milestone details cannot be null");
        Objects.requireNonNull(projectId, "Project id cannot be null");
        Objects.requireNonNull(initiatorId, "Initiator id cannot be null");
    }

    public static CreateMilestoneCommandBuilder builder() {
        return new CreateMilestoneCommandBuilder();
    }

    public static class CreateMilestoneCommandBuilder {
        private MilestoneValue milestoneDetails = null;
        private UUID projectId = null;
        private UUID initiatorId = null;

        public CreateMilestoneCommandBuilder() {
        }

        public CreateMilestoneCommandBuilder milestoneDetails(final MilestoneValue milestoneDetails) {
            this.milestoneDetails = milestoneDetails;
            return this;
        }

        public CreateMilestoneCommandBuilder projectId(final UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public CreateMilestoneCommandBuilder initiatorId(final UUID initiatorId) {
            this.initiatorId = initiatorId;
            return this;
        }

        public CreateMilestoneCommand build() {
            return new CreateMilestoneCommand(this.milestoneDetails, this.projectId, this.initiatorId);
        }
    }
}
