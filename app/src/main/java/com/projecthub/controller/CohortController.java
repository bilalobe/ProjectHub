package com.projecthub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.projecthub.model.Cohort;
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
     * @return a list of all Cohorts
     */
    @Operation(summary = "Get all Cohorts")
    @GetMapping
    public List<Cohort> getAllCohorts() {
        return cohortService.getAllCohorts();
    }

    /**
     * Retrieves all Cohorts associated with a specific School.
     *
     * @param schoolId the ID of the School
     * @return a list of Cohorts
     */
    @Operation(summary = "Get Cohorts by School ID")
    @GetMapping("/school/{schoolId}")
    public List<Cohort> getCohortsBySchoolId(@PathVariable Long schoolId) {
        return cohortService.getCohortsBySchoolId(schoolId);
    }

    /**
     * Retrieves a Cohort by its ID.
     *
     * @param id the ID of the Cohort
     * @return the Cohort
     */
    @Operation(summary = "Get Cohort by ID")
    @GetMapping("/{id}")
    public Cohort getCohortById(@PathVariable Long id) {
        return cohortService.getCohortById(id)
                .orElseThrow(() -> new RuntimeException("Cohort not found"));
    }

    /**
     * Creates a new Cohort.
     *
     * @param cohort the Cohort to create
     * @return the created Cohort
     */
    @Operation(summary = "Create a new Cohort")
    @PostMapping
    public Cohort createCohort(@RequestBody Cohort cohort) {
        return cohortService.saveCohort(cohort);
    }

    /**
     * Updates an existing Cohort.
     *
     * @param id     the ID of the Cohort to update
     * @param cohort the updated Cohort details
     * @return the updated Cohort
     */
    @Operation(summary = "Update an existing Cohort")
    @PutMapping("/{id}")
    public Cohort updateCohort(@PathVariable Long id, @RequestBody Cohort cohort) {
        cohort.setId(id);
        return cohortService.saveCohort(cohort);
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