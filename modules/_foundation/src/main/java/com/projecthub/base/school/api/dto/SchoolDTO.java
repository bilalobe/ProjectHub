package com.projecthub.base.school.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SchoolDTO(
    UUID id,

    @NotBlank(message = "School name is required")
    @Size(max = 100)
    String name,

    @NotNull
    SchoolAddressDTO address,

    @NotNull
    SchoolContactDTO contact,

    @NotNull
    SchoolIdentifierDTO identifier,
    boolean archived
) {
}
