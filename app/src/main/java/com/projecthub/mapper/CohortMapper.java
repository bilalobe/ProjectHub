package com.projecthub.mapper;

import org.springframework.stereotype.Component;

import com.projecthub.dto.CohortSummary;
import com.projecthub.model.Cohort;
import com.projecthub.model.School;

@Component
public class CohortMapper {

    public Cohort toCohort(CohortSummary cohortSummary, School school) {
        if (cohortSummary == null || school == null) {
            return null;
        }
        Cohort cohort = new Cohort();
        cohort.setId(cohortSummary.getId());
        cohort.setName(cohortSummary.getName());
        cohort.setSchool(school);
        return cohort;
    }

    public CohortSummary toCohortSummary(Cohort cohort) {
        if (cohort == null) {
            return null;
        }
        return new CohortSummary(
            cohort.getId(),
            cohort.getName(),
            cohort.getSchool()
        );
    }
}