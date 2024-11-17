package com.projecthub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projecthub.model.Cohort;
import com.projecthub.repository.jpa.CohortRepository;

/**
 * Service class for managing Cohorts.
 */
@Service
public class CohortService {

    private final CohortRepository cohortRepository;

    /**
     * Constructs a new CohortService.
     *
     * @param cohortRepository the Cohort repository
     */
    @Autowired
    public CohortService(CohortRepository cohortRepository) {
        this.cohortRepository = cohortRepository;
    }

    /**
     * Retrieves all Cohorts.
     *
     * @return a list of all Cohorts
     */
    public List<Cohort> getAllCohorts() {
        return cohortRepository.findAll();
    }

    /**
     * Retrieves all Cohorts associated with a specific School.
     *
     * @param schoolId the ID of the School
     * @return a list of Cohorts
     */
    public List<Cohort> getCohortsBySchoolId(Long schoolId) {
        return cohortRepository.findBySchoolId(schoolId);
    }

    /**
     * Retrieves a Cohort by its ID.
     *
     * @param id the ID of the Cohort
     * @return an Optional containing the Cohort if found
     */
    public Optional<Cohort> getCohortById(Long id) {
        return cohortRepository.findById(id);
    }

    /**
     * Saves a Cohort.
     *
     * @param cohort the Cohort to save
     * @return the saved Cohort
     */
    public Cohort saveCohort(Cohort cohort) {
        return cohortRepository.save(cohort);
    }

    /**
     * Deletes a Cohort by its ID.
     *
     * @param id the ID of the Cohort to delete
     */
    public void deleteCohort(Long id) {
        cohortRepository.deleteById(id);
    }
}