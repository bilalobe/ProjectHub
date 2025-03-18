package com.projecthub.base.cohort.domain.value;

import com.projecthub.base.cohort.domain.enums.GradeLevel;
import com.projecthub.base.shared.exception.ValidationException;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Embeddable
public record CohortAssignment(
    @NotNull UUID cohortId,

    @NotNull
    @Enumerated(EnumType.STRING)
    SchoolYear year,

    @NotNull
    @Enumerated(EnumType.STRING)
    GradeLevel level,

    @Min(1L) @Max(50L)
    int maxStudents,

    @Min(1L) @Max(10L)
    int maxTeams
) {
    public CohortAssignment {
        if (0 >= maxStudents || 50 < maxStudents) {
            throw new ValidationException("Cohort size must be between 1 and 50 students");
        }
        if (null == cohortId || null == year || null == level) {
            throw new ValidationException("Cohort assignment requires all fields");
        }
    }

    public CohortAssignment() {

    }

    public static CohortAssignment create(final UUID cohortId, final SchoolYear year, final GradeLevel level, final int maxStudents, final int maxTeams) {
        return new CohortAssignment(cohortId, year, level, maxStudents, maxTeams);
    }
}
