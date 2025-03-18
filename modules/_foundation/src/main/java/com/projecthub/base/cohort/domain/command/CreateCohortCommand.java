package com.projecthub.base.cohort.domain.command;

import com.projecthub.base.cohort.domain.enums.GradeLevel;
import com.projecthub.base.cohort.domain.validation.CohortValidation;
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
public class CreateCohortCommand {

    @NotNull(message = "School ID is required", groups = CohortValidation.Create.class)
    UUID schoolId;

    @NotBlank(message = "Cohort name is required", groups = CohortValidation.Create.class)
    @Size(max = 100, message = "Cohort name must be less than 100 characters", groups = CohortValidation.Create.class)
    String name;

    @NotNull(message = "Start term is required", groups = CohortValidation.Create.class)
    LocalDate startTerm;

    @NotNull(message = "End term is required", groups = CohortValidation.Create.class)
    LocalDate endTerm;

    @NotNull(message = "School year is required", groups = CohortValidation.Create.class)
    SchoolYear year;

    @NotNull(message = "Grade level is required", groups = CohortValidation.Create.class)
    GradeLevel level;

    @NotNull(message = "Maximum students is required", groups = CohortValidation.Create.class)
    Integer maxStudents;
}
