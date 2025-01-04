package com.projecthub.base.cohort.application.service;

import com.projecthub.base.cohort.api.dto.CohortDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CohortQuery {
    CohortDTO getCohortById(UUID id);

    Page<CohortDTO> getAllCohorts(Pageable pageable);

    Page<CohortDTO> getCohortsBySchoolId(UUID schoolId, Pageable pageable);

    Page<CohortDTO> getActiveCohorts(Pageable pageable);

    Page<CohortDTO> getArchivedCohorts(Pageable pageable);

    Page<CohortDTO> searchCohorts(String query, Pageable pageable);
}
