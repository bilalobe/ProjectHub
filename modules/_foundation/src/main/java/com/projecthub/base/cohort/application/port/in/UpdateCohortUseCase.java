package com.projecthub.base.cohort.application.port.in;

import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.base.cohort.domain.command.UpdateCohortCommand;

public interface UpdateCohortUseCase {
    CohortDTO updateCohort(UpdateCohortCommand command);
}
