package com.projecthub.core.controllers;

import com.projecthub.core.dto.CohortDTO;
import com.projecthub.core.services.school.CohortService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing Cohorts.
 */
@Tag(name = "Cohort Controller", description = "Operations related to Cohorts")
@RestController
@RequestMapping("/api/cohorts")
public class CohortController {

    private static final Logger logger = LoggerFactory.getLogger(CohortController.class);

    private final CohortService cohortService;

    /**
     * Constructs a new CohortController.
     *
     * @param cohortService the Cohort service
     */
    public CohortController(CohortService cohortService) {
        this.cohortService = cohortService;
    }

    /**
     * Retrieves all Cohorts.
     *
     * @return a list of all CohortDTO objects
     */
    @Operation(summary = "Get all Cohorts")
    @GetMapping
    public ResponseEntity<List<CohortDTO>> getAllCohorts() {
        logger.info("Retrieving all cohorts");
        List<CohortDTO> cohorts = cohortService.getAllCohorts();
        return ResponseEntity.ok(cohorts);
    }

    /**
     * Retrieves all Cohorts associated with a specific School.
     *
     * @param schoolId the ID of the School
     * @return a list of CohortDTO objects
     */
    @Operation(summary = "Get Cohorts by School ID")
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<CohortDTO>> getCohortsBySchoolId(@PathVariable UUID schoolId) {
        logger.info("Retrieving cohorts for school ID {}", schoolId);
        List<CohortDTO> cohorts = cohortService.getCohortsBySchoolId(schoolId);
        return ResponseEntity.ok(cohorts);
    }

    /**
     * Retrieves a Cohort by its ID.
     *
     * @param id the ID of the Cohort
     * @return the CohortDTO
     */
    @Operation(summary = "Get Cohort by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CohortDTO> getCohortById(@PathVariable UUID id) {
        logger.info("Retrieving cohort with ID {}", id);
        CohortDTO cohort = cohortService.getCohortById(id);
        return ResponseEntity.ok(cohort);
    }

    /**
     * Creates a new Cohort.
     *
     * @param cohortDTO the CohortDTO to create
     * @return the created CohortDTO
     */
    @Operation(summary = "Create a new Cohort")
    @PostMapping
    public ResponseEntity<CohortDTO> createCohort(@Valid @RequestBody CohortDTO cohortDTO) {
        logger.info("Creating a new cohort");
        CohortDTO createdCohort = cohortService.saveCohort(cohortDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCohort);
    }

    /**
     * Updates an existing Cohort.
     *
     * @param id        the ID of the Cohort to update
     * @param cohortDTO the updated CohortDTO details
     * @return the updated CohortDTO
     */
    @Operation(summary = "Update an existing Cohort")
    @PutMapping("/{id}")
    public ResponseEntity<CohortDTO> updateCohort(@PathVariable UUID id, @Valid @RequestBody CohortDTO cohortDTO) {
        logger.info("Updating cohort with ID {}", id);
        CohortDTO updatedCohort = cohortService.updateCohort(id, cohortDTO);
        return ResponseEntity.ok(updatedCohort);
    }

    /**
     * Deletes a Cohort by its ID.
     *
     * @param id the ID of the Cohort to delete
     */
    @Operation(summary = "Delete a Cohort by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCohort(@PathVariable UUID id) {
        logger.info("Deleting cohort with ID {}", id);
        cohortService.deleteCohort(id);
        return ResponseEntity.noContent().build();
    }
}