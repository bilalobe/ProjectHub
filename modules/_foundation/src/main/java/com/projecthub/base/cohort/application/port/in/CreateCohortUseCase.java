package com.projecthub.base.cohort.application.port.in;

import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.base.cohort.domain.command.CreateCohortCommand;

public interface CreateCohortUseCase {
    CohortDTO createCohort(CreateCohortCommand command);
}
