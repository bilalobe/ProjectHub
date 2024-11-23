package com.projecthub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.projecthub.dto.CohortSummary;
import com.projecthub.service.CohortService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller for managing Cohorts.
 */
@Tag(name = "Cohort Controller", description = "Operations related to Cohorts")
@RestController
@RequestMapping("/api/cohorts")
public class CohortController {

    private final CohortService cohortService;

    /**
     * Constructs a new CohortController.
     *
     * @param cohortService the Cohort service
     */
    @Autowired
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
    public List<CohortSummary> getAllCohorts() {
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
    public List<CohortSummary> getCohortsBySchoolId(@PathVariable Long schoolId) {
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
    public CohortSummary getCohortById(@PathVariable Long id) {
        return cohortService.getCohortById(id)
                .orElseThrow(() -> new RuntimeException("Cohort not found"));
    }

    /**
     * Creates a new Cohort.
     *
     * @param cohortSummary the CohortSummary to create
     * @return the created CohortSummary
     */
    @Operation(summary = "Create a new Cohort")
    @PostMapping
    public CohortSummary createCohort(@RequestBody CohortSummary cohortSummary) {
        return cohortService.saveCohort(cohortSummary);
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
    public CohortSummary updateCohort(@PathVariable Long id, @RequestBody CohortSummary cohortSummary) {
        cohortSummary.setId(id);
        return cohortService.saveCohort(cohortSummary);
    }

    /**
     * Deletes a Cohort by its ID.
     *
     * @param id the ID of the Cohort to delete
     */
    @Operation(summary = "Delete a Cohort by ID")
    @DeleteMapping("/{id}")
    public void deleteCohort(@PathVariable Long id) {
        cohortService.deleteCohort(id);
    }
}