package com.projecthub.mapper;

import com.projecthub.dto.CohortSummary;
import com.projecthub.model.Cohort;
import com.projecthub.model.School;

public class CohortMapper {

    public static Cohort toCohort(CohortSummary cohortSummary, School school) {
        Cohort cohort = new Cohort();
        cohort.setId(cohortSummary.getId());
        cohort.setName(cohortSummary.getName());
        cohort.setSchool(school);
        return cohort;
    }

    public static CohortSummary toCohortSummary(Cohort cohort) {
        return new CohortSummary(
            cohort.getId(),
            cohort.getName(),
            cohort.getSchool()
        );
    }
}