package com.projecthub.base.cohort.application.service;

import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.base.cohort.domain.command.CreateCohortCommand;
import com.projecthub.base.cohort.domain.command.DeleteCohortCommand;
import com.projecthub.base.cohort.domain.command.UpdateCohortCommand;

public interface CohortCommand {
    CohortDTO createCohort(CreateCohortCommand command);

    CohortDTO updateCohort(UpdateCohortCommand command);

    void deleteCohort(DeleteCohortCommand command);
}
