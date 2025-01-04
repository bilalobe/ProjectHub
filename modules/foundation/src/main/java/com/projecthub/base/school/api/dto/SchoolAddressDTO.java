package com.projecthub.base.school.api.dto;

import jakarta.validation.constraints.NotBlank;


public record SchoolAddressDTO(
    @NotBlank(message = "Street is required")
    String street,

    @NotBlank(message = "City is required")
    String city,

    @NotBlank(message = "State is required")
    String state,

    @NotBlank(message = "Postal code is required")
    String postalCode,

    String country
) {
}
