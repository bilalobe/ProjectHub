package com.projecthub.base.milestone.domain.command;

import com.projecthub.base.milestone.domain.enums.MilestoneStatus;
import com.projecthub.base.milestone.domain.value.MilestoneValue;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public record UpdateMilestoneCommand(
    @NotNull(message = "Milestone id is required")
    UUID id,
    MilestoneValue milestoneDetails,
    MilestoneStatus targetStatus,
    UUID initiatorId
) {
    public UpdateMilestoneCommand {
        Objects.requireNonNull(id, "Milestone id cannot be null");
        Objects.requireNonNull(milestoneDetails, "Milestone details cannot be null");
        Objects.requireNonNull(targetStatus, "Milestone target status cannot be null");
        Objects.requireNonNull(initiatorId, "Initiator id cannot be null");
    }

    public static UpdateMilestoneCommandBuilder builder() {
        return new UpdateMilestoneCommandBuilder();
    }

    public static class UpdateMilestoneCommandBuilder {
        private UUID id;
        private MilestoneValue milestoneDetails;
        private MilestoneStatus targetStatus;
        private UUID initiatorId;
        public UpdateMilestoneCommandBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UpdateMilestoneCommandBuilder milestoneDetails(MilestoneValue milestoneDetails) {
            this.milestoneDetails = milestoneDetails;
            return this;
        }

        public UpdateMilestoneCommandBuilder targetStatus(MilestoneStatus targetStatus) {
            this.targetStatus = targetStatus;
            return this;
        }

        public UpdateMilestoneCommandBuilder initiatorId(UUID initiatorId) {
            this.initiatorId = initiatorId;
            return this;
        }

        public UpdateMilestoneCommand build() {
            return new UpdateMilestoneCommand(id, milestoneDetails, targetStatus, initiatorId);
        }
    }
}
