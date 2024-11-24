package com.projecthub.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projecthub.dto.CohortSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.CohortMapper;
import com.projecthub.model.Cohort;
import com.projecthub.model.School;
import com.projecthub.repository.jpa.CohortRepository;
import com.projecthub.repository.jpa.SchoolRepository;

/**
 * Service class for managing Cohorts.
 */
@Service
public class CohortService {

    private static final Logger logger = LoggerFactory.getLogger(CohortService.class);

    private final CohortRepository cohortRepository;
    private final SchoolRepository schoolRepository;
    private final CohortMapper cohortMapper;

    /**
     * Constructs a new CohortService.
     *
     * @param cohortRepository the Cohort repository
     * @param schoolRepository the School repository
     * @param cohortMapper the Cohort mapper
     */
    public CohortService(CohortRepository cohortRepository, SchoolRepository schoolRepository, CohortMapper cohortMapper) {
        this.cohortRepository = cohortRepository;
        this.schoolRepository = schoolRepository;
        this.cohortMapper = cohortMapper;
    }

    /**
     * Retrieves all cohorts.
     *
     * @return a list of CohortSummary
     */
    public List<CohortSummary> getAllCohorts() {
        logger.info("Retrieving all cohorts");
        return cohortRepository.findAll().stream()
                .map(cohortMapper::toCohortSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves cohorts by school ID.
     *
     * @param schoolId the school ID
     * @return a list of CohortSummary
     * @throws IllegalArgumentException if schoolId is null
     */
    public List<CohortSummary> getCohortsBySchoolId(Long schoolId) {
        logger.info("Retrieving cohorts for school ID {}", schoolId);
        if (schoolId == null) {
            throw new IllegalArgumentException("School ID cannot be null");
        }
        return cohortRepository.findBySchoolId(schoolId).stream()
                .map(cohortMapper::toCohortSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a cohort by its ID.
     *
     * @param id the cohort ID
     * @return the CohortSummary
     * @throws IllegalArgumentException if id is null
     * @throws ResourceNotFoundException if the cohort is not found
     */
    public CohortSummary getCohortById(Long id) throws ResourceNotFoundException {
        logger.info("Retrieving cohort with ID {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Cohort ID cannot be null");
        }
        return cohortRepository.findById(id)
                .map(cohortMapper::toCohortSummary)
                .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + id));
    }

    /**
     * Saves a cohort.
     *
     * @param cohortSummary the CohortSummary to save
     * @return the saved CohortSummary
     * @throws IllegalArgumentException if cohortSummary is null
     */
    @Transactional
    public CohortSummary saveCohort(CohortSummary cohortSummary) {
        logger.info("Saving cohort");
        if (cohortSummary == null) {
            throw new IllegalArgumentException("CohortSummary cannot be null");
        }
        School school = schoolRepository.findById(cohortSummary.getSchool().getId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + cohortSummary.getSchool().getId()));
        Cohort cohort = cohortMapper.toCohort(cohortSummary, school);
        Cohort savedCohort = cohortRepository.save(cohort);
        logger.info("Cohort saved with ID {}", savedCohort.getId());
        return cohortMapper.toCohortSummary(savedCohort);
    }

    /**
     * Deletes a cohort by its ID.
     *
     * @param id the cohort ID to delete
     * @throws IllegalArgumentException if id is null
     * @throws ResourceNotFoundException if the cohort is not found
     */
    @Transactional
    public void deleteCohort(Long id) throws ResourceNotFoundException {
        logger.info("Deleting cohort with ID {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Cohort ID cannot be null");
        }
        if (!cohortRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cohort not found with ID: " + id);
        }
        cohortRepository.deleteById(id);
        logger.info("Cohort deleted");
    }

    public CohortSummary updateCohort(Long id, CohortSummary cohortSummary) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}