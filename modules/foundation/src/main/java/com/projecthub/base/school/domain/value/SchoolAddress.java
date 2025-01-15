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

    public static SchoolAddress from(final SchoolAddress other) {
        return new SchoolAddress(
            other.street(),
            other.city(),
            other.state(),
            other.postalCode(),
            other.country()
        );
    }

    public static SchoolAddress of(final String street, final String city, final String state, final String postalCode, final String country) {
        return new SchoolAddress(street, city, state, postalCode, country);
    }

    public String formatted() {
        return String.format("%s, %s, %s %s", this.street, this.city, this.state, this.postalCode);
    }
}
