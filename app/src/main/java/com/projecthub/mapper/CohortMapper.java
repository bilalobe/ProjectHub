package com.projecthub.mapper;

import com.projecthub.dto.CohortSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.model.Cohort;
import com.projecthub.model.School;
import com.projecthub.repository.SchoolRepository;
import org.springframework.stereotype.Component;

@Component
public class CohortMapper {

    private final SchoolRepository schoolRepository;

    public CohortMapper(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    public Cohort toCohort(CohortSummary cohortSummary) {
        if (cohortSummary == null) {
            return null;
        }

        School school = schoolRepository.findById(cohortSummary.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + cohortSummary.getSchoolId()));

        Cohort cohort = new Cohort();
        cohort.setName(cohortSummary.getName());
        cohort.setSchool(school);

        return cohort;
    }

    public CohortSummary toCohortSummary(Cohort cohort) {
        if (cohort == null) {
            return null;
        }

        Long schoolId = (cohort.getSchool() != null) ? cohort.getSchool().getId() : null;

        return new CohortSummary(
                cohort.getId(),
                cohort.getName(),
                schoolId
        );
    }

    public void updateCohortFromSummary(CohortSummary cohortSummary, Cohort cohort) {
        if (cohortSummary == null || cohort == null) {
            return;
        }

        if (cohortSummary.getSchoolId() != null) {
            School school = schoolRepository.findById(cohortSummary.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + cohortSummary.getSchoolId()));
            cohort.setSchool(school);
        }

        cohort.setName(cohortSummary.getName());
    }
}