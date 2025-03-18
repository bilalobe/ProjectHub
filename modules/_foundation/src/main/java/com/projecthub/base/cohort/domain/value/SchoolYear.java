package com.projecthub.base.cohort.domain.value;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

@Embeddable
public record SchoolYear(
    @NotNull Integer startYear,
    @NotNull Integer endYear
) {
    public SchoolYear(@NotNull Integer startYear, @NotNull Integer endYear) {
        if (endYear != startYear + 1) {
            throw new IllegalArgumentException("School year must span consecutive years");
        }
        this.startYear = startYear;
        this.endYear = endYear;
    }

    public SchoolYear() {

    }
}
