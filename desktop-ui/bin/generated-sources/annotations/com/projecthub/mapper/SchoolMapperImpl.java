package com.projecthub.mapper;

import com.projecthub.dto.SchoolDTO;
import com.projecthub.model.Cohort;
import com.projecthub.model.School;
import com.projecthub.model.Team;

import java.util.List;
import javax.annotation.processing.Generated;

import org.springframework.stereotype.Component;

@Generated(
        value = "org.mapstruct.ap.MappingProcessor",
        date = "2024-12-08T14:18:23+0100",
        comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.40.0.z20241112-1021, environment: Java 17.0.13 (Eclipse Adoptium)"
)
@Component
public class SchoolMapperImpl extends SchoolMapper {

    @Override
    public School toSchool(SchoolDTO schoolDTO) {
        if (schoolDTO == null) {
            return null;
        }

        School school = new School();

        school.setTeams(mapTeamIdsToTeams(schoolDTO.getTeamIds()));
        school.setCohorts(mapCohortIdsToCohorts(schoolDTO.getCohortIds()));
        school.setId(schoolDTO.getId());
        school.setName(schoolDTO.getName());
        school.setAddress(schoolDTO.getAddress());
        school.setCreatedAt(schoolDTO.getCreatedAt());
        school.setUpdatedAt(schoolDTO.getUpdatedAt());
        school.setCreatedBy(schoolDTO.getCreatedBy());
        school.setDeleted(schoolDTO.isDeleted());

        return school;
    }

    @Override
    public SchoolDTO toSchoolDTO(School school) {
        if (school == null) {
            return null;
        }

        SchoolDTO schoolDTO = new SchoolDTO();

        schoolDTO.setTeamIds(mapTeamsToTeamIds(school.getTeams()));
        schoolDTO.setCohortIds(mapCohortsToCohortIds(school.getCohorts()));
        schoolDTO.setId(school.getId());
        schoolDTO.setName(school.getName());
        schoolDTO.setAddress(school.getAddress());
        schoolDTO.setCreatedAt(school.getCreatedAt());
        schoolDTO.setUpdatedAt(school.getUpdatedAt());
        schoolDTO.setCreatedBy(school.getCreatedBy());
        schoolDTO.setDeleted(school.isDeleted());

        return schoolDTO;
    }

    @Override
    public void updateSchoolFromDTO(SchoolDTO schoolDTO, School school) {
        if (schoolDTO == null) {
            return;
        }

        if (school.getTeams() != null) {
            List<Team> list = mapTeamIdsToTeams(schoolDTO.getTeamIds());
            if (list != null) {
                school.getTeams().clear();
                school.getTeams().addAll(list);
            } else {
                school.setTeams(null);
            }
        } else {
            List<Team> list = mapTeamIdsToTeams(schoolDTO.getTeamIds());
            if (list != null) {
                school.setTeams(list);
            }
        }
        if (school.getCohorts() != null) {
            List<Cohort> list1 = mapCohortIdsToCohorts(schoolDTO.getCohortIds());
            if (list1 != null) {
                school.getCohorts().clear();
                school.getCohorts().addAll(list1);
            } else {
                school.setCohorts(null);
            }
        } else {
            List<Cohort> list1 = mapCohortIdsToCohorts(schoolDTO.getCohortIds());
            if (list1 != null) {
                school.setCohorts(list1);
            }
        }
        school.setId(schoolDTO.getId());
        school.setName(schoolDTO.getName());
        school.setAddress(schoolDTO.getAddress());
        school.setCreatedAt(schoolDTO.getCreatedAt());
        school.setUpdatedAt(schoolDTO.getUpdatedAt());
        school.setCreatedBy(schoolDTO.getCreatedBy());
        school.setDeleted(schoolDTO.isDeleted());
    }
}
