package com.projecthub.base.school.application.port.in.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public record ArchiveSchoolCommand(

    @NotNull(message = "School ID is required")
    UUID id,

    @NotBlank(message = "Archive reason is required")
    String reason,

    UUID initiatorId
) {
    public ArchiveSchoolCommand {
        Objects.requireNonNull(reason, "Archive reason cannot be null");
    }

    public static ArchiveSchoolCommandBuilder builder() {
        return new ArchiveSchoolCommandBuilder();
    }

    public static class ArchiveSchoolCommandBuilder {
        private UUID id = null;
        private String reason = null;
        private UUID initiatorId = null;

        public ArchiveSchoolCommandBuilder() {
        }

        public ArchiveSchoolCommandBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        public ArchiveSchoolCommandBuilder reason(final String reason) {
            this.reason = reason;
            return this;
        }

        public ArchiveSchoolCommandBuilder initiatorId(final UUID initiatorId) {
            this.initiatorId = initiatorId;
            return this;
        }

        public ArchiveSchoolCommand build() {
            return new ArchiveSchoolCommand(this.id, this.reason, this.initiatorId);
        }
    }
}
