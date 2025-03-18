package com.projecthub.base.cohort.api.input;

import com.projecthub.base.cohort.domain.enums.GradeLevel;
import com.projecthub.base.cohort.domain.value.SchoolYear;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder
public class UpdateCohortInput {
    @NotBlank(message = "Cohort name is required")
    @Size(max = 100, message = "Cohort name must be less than 100 characters")
    String name;

    @NotNull(message = "Start term is required")
    LocalDate startTerm;

    @NotNull(message = "End term is required")
    LocalDate endTerm;

    @NotNull(message = "School year is required")
    SchoolYear year;

    @NotNull(message = "Grade level is required")
    GradeLevel level;

    @NotNull(message = "Maximum students is required")
    Integer maxStudents;

    @NotNull(message = "School ID is required")
    UUID schoolId;
}
