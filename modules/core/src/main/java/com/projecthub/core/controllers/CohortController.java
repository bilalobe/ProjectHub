package com.projecthub.core.controllers;

import com.projecthub.core.api.CohortApi;
import com.projecthub.core.dto.CohortDTO;
import com.projecthub.core.services.school.CohortService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cohorts")
public class CohortController implements CohortApi {

    private static final Logger logger = LoggerFactory.getLogger(CohortController.class);
    private final CohortService cohortService;

    public CohortController(CohortService cohortService) {
        this.cohortService = cohortService;
    }

    @Override
    public ResponseEntity<List<CohortDTO>> getAllCohorts() {
        logger.info("Retrieving all cohorts");
        return ResponseEntity.ok(cohortService.getAllCohorts());
    }

    @Override
    public ResponseEntity<CohortDTO> getById(UUID id) {
        logger.info("Retrieving cohort with ID {}", id);
        return ResponseEntity.ok(cohortService.getCohortById(id));
    }

    @Override
    public ResponseEntity<CohortDTO> createCohort(@Valid @RequestBody CohortDTO cohort) {
        logger.info("Creating new cohort");
        return ResponseEntity.ok(cohortService.saveCohort(cohort));
    }

    @Override
    public ResponseEntity<CohortDTO> updateCohort(UUID id, @Valid @RequestBody CohortDTO cohort) {
        logger.info("Updating cohort with ID {}", id);
        return ResponseEntity.ok(cohortService.updateCohort(id, cohort));
    }

    @Override
    public ResponseEntity<Void> deleteById(UUID id) {
        logger.info("Deleting cohort with ID {}", id);
        cohortService.deleteCohort(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<CohortDTO>> getCohortsBySchoolId(UUID schoolId) {
        logger.info("Retrieving cohorts for school with ID {}", schoolId);
        return ResponseEntity.ok(cohortService.getCohortsBySchoolId(schoolId));
    }
}