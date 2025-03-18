package com.projecthub.base.school.api.dto;

import com.projecthub.base.school.domain.enums.SchoolType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SchoolIdentifierDTO(
    @NotBlank(message = "Code is required")
    String code,

    @NotNull(message = "Type is required")
    SchoolType type,

    @NotBlank(message = "District is required")
    String district,

    String formatted
) {
}
