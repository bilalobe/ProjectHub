package com.projecthub.mapper;

import com.projecthub.dto.SchoolDTO;
import com.projecthub.model.Cohort;
import com.projecthub.model.School;
import com.projecthub.model.Team;
import com.projecthub.repository.CohortRepository;
import com.projecthub.repository.TeamRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class SchoolMapper {

    private TeamRepository teamRepository;

    private CohortRepository cohortRepository;

    @Mapping(source = "teamIds", target = "teams", qualifiedByName = "mapTeamIdsToTeams")
    @Mapping(source = "cohortIds", target = "cohorts", qualifiedByName = "mapCohortIdsToCohorts")
    public abstract School toSchool(SchoolDTO schoolDTO);

    @Mapping(source = "teams", target = "teamIds", qualifiedByName = "mapTeamsToTeamIds")
    @Mapping(source = "cohorts", target = "cohortIds", qualifiedByName = "mapCohortsToCohortIds")
    public abstract SchoolDTO toSchoolDTO(School school);

    @Mapping(source = "teamIds", target = "teams", qualifiedByName = "mapTeamIdsToTeams")
    @Mapping(source = "cohortIds", target = "cohorts", qualifiedByName = "mapCohortIdsToCohorts")
    public abstract void updateSchoolFromDTO(SchoolDTO schoolDTO, @MappingTarget School school);

    @Named("mapTeamIdsToTeams")
    protected List<Team> mapTeamIdsToTeams(List<UUID> teamIds) {
        return teamIds.stream()
                .map(id -> teamRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid team ID: " + id)))
                .toList();
    }

    @Named("mapTeamsToTeamIds")
    protected List<UUID> mapTeamsToTeamIds(List<Team> teams) {
        return teams.stream()
                .map(Team::getId)
                .toList();
    }

    @Named("mapCohortIdsToCohorts")
    protected List<Cohort> mapCohortIdsToCohorts(List<UUID> cohortIds) {
        return cohortIds.stream()
                .map(id -> cohortRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid cohort ID: " + id)))
                .toList();
    }

    @Named("mapCohortsToCohortIds")
    protected List<UUID> mapCohortsToCohortIds(List<Cohort> cohorts) {
        return cohorts.stream()
                .map(Cohort::getId)
                .toList();
    }
}