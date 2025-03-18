package com.projecthub.base.cohort.api.controller;

import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.base.cohort.api.rest.CohortApi;
import com.projecthub.base.cohort.application.service.CohortCommandService;
import com.projecthub.base.cohort.application.service.CohortQueryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cohorts")
public class CohortController implements CohortApi {

    private static final Logger logger = LoggerFactory.getLogger(CohortController.class);
    private final CohortCommandService cohortCommandService;
    private final CohortQueryService cohortQueryService;

    public CohortController(final CohortCommandService cohortCommandService, final CohortQueryService cohortQueryService) {
        this.cohortCommandService = cohortCommandService;
        this.cohortQueryService = cohortQueryService;
    }

    @Override
    public ResponseEntity<List<CohortDTO>> getAllCohorts() {
        CohortController.logger.info("Retrieving all cohorts");
        return ResponseEntity.ok(this.cohortQueryService.getAllCohorts());
    }

    @Override
    public ResponseEntity<CohortDTO> getById(final UUID id) {
        CohortController.logger.info("Retrieving cohort with ID {}", id);
        return ResponseEntity.ok(this.cohortQueryService.getCohortById(id));
    }

    @Override
    public ResponseEntity<CohortDTO> createCohort(@Valid @RequestBody final CohortDTO cohort) {
        CohortController.logger.info("Creating new cohort");
        return ResponseEntity.ok(this.cohortCommandService.createCohort(cohort));
    }

    @Override
    public ResponseEntity<CohortDTO> updateCohort(final UUID id, @Valid @RequestBody final CohortDTO cohort) {
        CohortController.logger.info("Updating cohort with ID {}", id);
        return ResponseEntity.ok(this.cohortCommandService.updateCohort(id, cohort));
    }

    @Override
    public ResponseEntity<Void> deleteById(final UUID id) {
        CohortController.logger.info("Deleting cohort with ID {}", id);
        this.cohortCommandService.deleteCohort(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<CohortDTO>> getCohortsBySchoolId(final UUID schoolId) {
        CohortController.logger.info("Retrieving cohorts for school with ID {}", schoolId);
        return ResponseEntity.ok(this.cohortQueryService.getCohortsBySchoolId(schoolId));
    }

    @Override
    public ResponseEntity<Page<CohortDTO>> getCohorts(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCohorts'");
    }

    @Override
    public ResponseEntity<List<CohortDTO>> getActiveCohortsBySchool(UUID schoolId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getActiveCohortsBySchool'");
    }

    @Override
    public ResponseEntity<CohortDTO> archiveCohort(UUID id, String reason) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'archiveCohort'");
    }

    @Override
    public ResponseEntity<CohortDTO> completeCohort(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'completeCohort'");
    }

    @Override
    public ResponseEntity<CohortDTO> addTeam(UUID id, UUID teamId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addTeam'");
    }
}
