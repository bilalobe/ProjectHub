package com.projecthub.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projecthub.dto.CohortDTO;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.service.CohortService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * @return a list of all CohortSummary objects
     */
    @Operation(summary = "Get all Cohorts")
    @GetMapping
    public List<CohortDTO> getAllCohorts() {
        return cohortService.getAllCohorts();
    }

    /**
     * Retrieves all Cohorts associated with a specific School.
     *
     * @param schoolId the ID of the School
     * @return a list of CohortSummary objects
     */
    @Operation(summary = "Get Cohorts by School ID")
    @GetMapping("/school/{schoolId}")
    public List<CohortDTO> getCohortsBySchoolId(@PathVariable UUID schoolId) {
        return cohortService.getCohortsBySchoolId(schoolId);
    }

    /**
     * Retrieves a Cohort by its ID.
     *
     * @param id the ID of the Cohort
     * @return the CohortSummary
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
     * @param cohortSummary the CohortSummary to create
     * @return the created CohortSummary
     */
    @Operation(summary = "Create a new Cohort")
    @PostMapping
    public ResponseEntity<CohortDTO> createCohort(@Valid @RequestBody CohortDTO cohortSummary) {
        try {
            CohortDTO createdCohort = cohortService.saveCohort(cohortSummary);
            return ResponseEntity.ok(createdCohort);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    /**
     * Updates an existing Cohort.
     *
     * @param id     the ID of the Cohort to update
     * @param cohortSummary the updated CohortSummary details
     * @return the updated CohortSummary
     */
    @Operation(summary = "Update an existing Cohort")
    @PutMapping("/{id}")
    public ResponseEntity<CohortDTO> updateCohort(@PathVariable UUID id, @Valid @RequestBody CohortDTO cohortSummary) {
        try {
            CohortDTO updatedCohort = cohortService.updateCohort(id, cohortSummary);
            return ResponseEntity.ok(updatedCohort);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    /**
     * Deletes a Cohort by its ID.
     *
     * @param id the ID of the Cohort to delete
     */
    @Operation(summary = "Delete a Cohort by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCohort(@PathVariable UUID id) {
        try {
            cohortService.deleteCohort(id);
            return ResponseEntity.ok("Cohort deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Cohort not found");
        }
    }

    @ExceptionHandler({ResourceNotFoundException.class, Exception.class})
    public ResponseEntity<String> handleExceptions(Exception ex) {
        logger.error("An error occurred", ex);
        return ResponseEntity.status(400).body(ex.getMessage());
    }
}