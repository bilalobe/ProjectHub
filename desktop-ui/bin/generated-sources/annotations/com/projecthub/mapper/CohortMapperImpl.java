package com.projecthub.mapper;

import com.projecthub.dto.CohortDTO;
import com.projecthub.model.Cohort;
import com.projecthub.model.School;

import java.util.UUID;
import javax.annotation.processing.Generated;

import org.springframework.stereotype.Component;

@Generated(
        value = "org.mapstruct.ap.MappingProcessor",
        date = "2024-12-08T14:18:23+0100",
        comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.40.0.z20241112-1021, environment: Java 17.0.13 (Eclipse Adoptium)"
)
@Component
public class CohortMapperImpl implements CohortMapper {

    @Override
    public Cohort toCohort(CohortDTO cohortDTO) {
        if (cohortDTO == null) {
            return null;
        }

        Cohort cohort = new Cohort();

        cohort.setSchool(cohortDTOToSchool(cohortDTO));
        cohort.setId(cohortDTO.getId());
        cohort.setName(cohortDTO.getName());

        return cohort;
    }

    @Override
    public CohortDTO toCohortDTO(Cohort cohort) {
        if (cohort == null) {
            return null;
        }

        CohortDTO cohortDTO = new CohortDTO();

        cohortDTO.setSchoolId(cohortSchoolId(cohort));
        cohortDTO.setId(cohort.getId());
        cohortDTO.setName(cohort.getName());

        return cohortDTO;
    }

    @Override
    public void updateCohortFromDTO(CohortDTO cohortDTO, Cohort cohort) {
        if (cohortDTO == null) {
            return;
        }

        if (cohort.getSchool() == null) {
            cohort.setSchool(new School());
        }
        cohortDTOToSchool1(cohortDTO, cohort.getSchool());
        cohort.setId(cohortDTO.getId());
        cohort.setName(cohortDTO.getName());
    }

    protected School cohortDTOToSchool(CohortDTO cohortDTO) {
        if (cohortDTO == null) {
            return null;
        }

        School school = new School();

        school.setId(cohortDTO.getSchoolId());

        return school;
    }

    private UUID cohortSchoolId(Cohort cohort) {
        School school = cohort.getSchool();
        if (school == null) {
            return null;
        }
        return school.getId();
    }

    protected void cohortDTOToSchool1(CohortDTO cohortDTO, School mappingTarget) {
        if (cohortDTO == null) {
            return;
        }

        mappingTarget.setId(cohortDTO.getSchoolId());
    }
}
