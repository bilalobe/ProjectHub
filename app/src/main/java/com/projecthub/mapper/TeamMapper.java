package com.projecthub.mapper;

import com.projecthub.dto.TeamDTO;
import com.projecthub.model.Team;
import com.projecthub.model.School;
import com.projecthub.model.Cohort;
import com.projecthub.repository.SchoolRepository;
import com.projecthub.repository.CohortRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class TeamMapper {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private CohortRepository cohortRepository;

    @Mapping(source = "schoolId", target = "school", qualifiedByName = "mapSchoolIdToSchool")
    @Mapping(source = "cohortId", target = "cohort", qualifiedByName = "mapCohortIdToCohort")
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    public abstract Team toTeam(TeamDTO teamDTO);

    @Mapping(source = "school.id", target = "schoolId")
    @Mapping(source = "cohort.id", target = "cohortId")
    public abstract TeamDTO toTeamDTO(Team team);

    @Mapping(source = "schoolId", target = "school", qualifiedByName = "mapSchoolIdToSchool")
    @Mapping(source = "cohortId", target = "cohort", qualifiedByName = "mapCohortIdToCohort")
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    public abstract void updateTeamFromDTO(TeamDTO teamDTO, @MappingTarget Team team);

    @Named("mapSchoolIdToSchool")
    protected School mapSchoolIdToSchool(UUID schoolId) {
        return schoolRepository.findById(schoolId).orElseThrow(() -> new IllegalArgumentException("Invalid school ID: " + schoolId));
    }

    @Named("mapSchoolToSchoolId")
    protected UUID mapSchoolToSchoolId(School school) {
        return school != null ? school.getId() : null;
    }

    @Named("mapCohortIdToCohort")
    protected Cohort mapCohortIdToCohort(UUID cohortId) {
        return cohortRepository.findById(cohortId).orElseThrow(() -> new IllegalArgumentException("Invalid cohort ID: " + cohortId));
    }

    @Named("mapCohortToCohortId")
    protected UUID mapCohortToCohortId(Cohort cohort) {
        return cohort != null ? cohort.getId() : null;
    }
}