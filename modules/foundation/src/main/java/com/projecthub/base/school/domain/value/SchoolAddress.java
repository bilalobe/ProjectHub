package com.projecthub.base.school.domain.value;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public record SchoolAddress(
    @NotBlank String street,
    @NotBlank String city,
    @NotBlank String state,
    @NotBlank String postalCode,
    String country
) {

    public static SchoolAddress from(SchoolAddress other) {
        return new SchoolAddress(
            other.street(),
            other.city(),
            other.state(),
            other.postalCode(),
            other.country()
        );
    }

    public static SchoolAddress of(String street, String city, String state, String postalCode, String country) {
        return new SchoolAddress(street, city, state, postalCode, country);
    }

    public String formatted() {
        return String.format("%s, %s, %s %s", street, city, state, postalCode);
    }
}
