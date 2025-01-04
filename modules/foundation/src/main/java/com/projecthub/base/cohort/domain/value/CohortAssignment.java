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

    @Min(1) @Max(50)
    int maxStudents,

    @Min(1) @Max(10)
    int maxTeams
) {
    public CohortAssignment {
        if (maxStudents <= 0 || maxStudents > 50) {
            throw new ValidationException("Cohort size must be between 1 and 50 students");
        }
        if (cohortId == null || year == null || level == null) {
            throw new ValidationException("Cohort assignment requires all fields");
        }
    }

    public static CohortAssignment create(UUID cohortId, SchoolYear year, GradeLevel level, int maxStudents, int maxTeams) {
        return new CohortAssignment(cohortId, year, level, maxStudents, maxTeams);
    }
}
