package com.projecthub.base.school.application.port.in.command;

import com.projecthub.base.school.domain.value.SchoolAddress;
import com.projecthub.base.school.domain.value.SchoolContact;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public record CreateSchoolCommand(
    @NotBlank(message = "School name is required")
    String name,

    @NotNull(message = "School address is required")
    SchoolAddress address,

    @NotNull(message = "School contact is required")
    SchoolContact contact,

    UUID initiatorId
) {
    public CreateSchoolCommand {
        Objects.requireNonNull(name, "School name cannot be null");
        Objects.requireNonNull(address, "School address cannot be null");
        Objects.requireNonNull(contact, "School contact cannot be null");
    }

    public static CreateSchoolCommandBuilder builder() {
        return new CreateSchoolCommandBuilder();
    }

    public static class CreateSchoolCommandBuilder {
        private String name;
        private SchoolAddress address;
        private SchoolContact contact;
        private UUID initiatorId;  // Add this

        public CreateSchoolCommandBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CreateSchoolCommandBuilder address(SchoolAddress address) {
            this.address = address;
            return this;
        }

        public CreateSchoolCommandBuilder contact(SchoolContact contact) {
            this.contact = contact;
            return this;
        }

        // Add this method
        public CreateSchoolCommandBuilder initiatorId(UUID initiatorId) {
            this.initiatorId = initiatorId;
            return this;
        }

        public CreateSchoolCommand build() {
            return new CreateSchoolCommand(name, address, contact, initiatorId); // Include initiatorId
        }
    }
}
