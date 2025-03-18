package com.projecthub.base.school.application.port.in.command;

import com.projecthub.base.school.domain.value.SchoolAddress;
import com.projecthub.base.school.domain.value.SchoolContact;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.sql.Update;

import java.util.Objects;
import java.util.UUID;

public record UpdateSchoolCommand(
    @NotNull(message = "School ID is required", groups = Update.class)
    UUID id,

    @NotBlank(message = "School name is required")
    String name,

    @NotNull(message = "School address is required")
    SchoolAddress address,

    @NotNull(message = "School contact is required")
    SchoolContact contact,

    UUID initiatorId
) {
    public UpdateSchoolCommand {
        Objects.requireNonNull(id, "School ID cannot be null");
        Objects.requireNonNull(name, "School name cannot be null");
        Objects.requireNonNull(address, "School address cannot be null");
        Objects.requireNonNull(contact, "School contact cannot be null");
    }

    public static UpdateSchoolCommandBuilder builder() {
        return new UpdateSchoolCommandBuilder();
    }

    public static class UpdateSchoolCommandBuilder {
        private UUID id = null;
        private String name = null;
        private SchoolAddress address = null;
        private SchoolContact contact = null;
        private UUID initiatorId = null;

        public UpdateSchoolCommandBuilder() {
        }

        public UpdateSchoolCommandBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        public UpdateSchoolCommandBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public UpdateSchoolCommandBuilder address(final SchoolAddress address) {
            this.address = address;
            return this;
        }

        public UpdateSchoolCommandBuilder contact(final SchoolContact contact) {
            this.contact = contact;
            return this;
        }

        public UpdateSchoolCommandBuilder initiatorId(final UUID initiatorId) {
            this.initiatorId = initiatorId;
            return this;
        }

        public UpdateSchoolCommand build() {
            return new UpdateSchoolCommand(this.id, this.name, this.address, this.contact, this.initiatorId);
        }
    }
}
