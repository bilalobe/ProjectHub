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
        private UUID id;
        private String reason;
        private UUID initiatorId;

        public ArchiveSchoolCommandBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public ArchiveSchoolCommandBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public ArchiveSchoolCommandBuilder initiatorId(UUID initiatorId) {
            this.initiatorId = initiatorId;
            return this;
        }

        public ArchiveSchoolCommand build() {
            return new ArchiveSchoolCommand(id, reason, initiatorId);
        }
    }
}
