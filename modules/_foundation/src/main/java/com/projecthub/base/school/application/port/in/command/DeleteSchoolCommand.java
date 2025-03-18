package com.projecthub.base.school.application.port.in.command;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public record DeleteSchoolCommand(
    @NotNull(message = "School ID is required")
    UUID id,

    UUID initiatorId
) {
    public DeleteSchoolCommand {
        Objects.requireNonNull(id, "School ID cannot be null");
    }

    public static DeleteSchoolCommandBuilder builder() {
        return new DeleteSchoolCommandBuilder();
    }

    public static class DeleteSchoolCommandBuilder {
        private UUID id = null;
        private UUID initiatorId = null;

        public DeleteSchoolCommandBuilder() {
        }

        public DeleteSchoolCommandBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        public DeleteSchoolCommandBuilder initiatorId(final UUID initiatorId) {
            this.initiatorId = initiatorId;
            return this;
        }

        public DeleteSchoolCommand build() {
            return new DeleteSchoolCommand(this.id, this.initiatorId);
        }
    }
}
