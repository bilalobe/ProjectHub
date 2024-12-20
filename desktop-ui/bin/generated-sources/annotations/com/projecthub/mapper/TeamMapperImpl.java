package com.projecthub.mapper;

import com.projecthub.dto.TeamDTO;
import com.projecthub.model.Cohort;
import com.projecthub.model.School;
import com.projecthub.model.Team;

import java.util.UUID;
import javax.annotation.processing.Generated;

import org.springframework.stereotype.Component;

@Generated(
        value = "org.mapstruct.ap.MappingProcessor",
        date = "2024-12-08T14:18:24+0100",
        comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.40.0.z20241112-1021, environment: Java 17.0.13 (Eclipse Adoptium)"
)
@Component
public class TeamMapperImpl extends TeamMapper {

    @Override
    public Team toTeam(TeamDTO teamDTO) {
        if (teamDTO == null) {
            return null;
        }

        Team team = new Team();

        team.setSchool(mapSchoolIdToSchool(teamDTO.getSchoolId()));
        team.setCohort(mapCohortIdToCohort(teamDTO.getCohortId()));
        team.setId(teamDTO.getId());
        team.setName(teamDTO.getName());

        return team;
    }

    @Override
    public TeamDTO toTeamDTO(Team team) {
        if (team == null) {
            return null;
        }

        TeamDTO teamDTO = new TeamDTO();

        teamDTO.setSchoolId(teamSchoolId(team));
        teamDTO.setCohortId(teamCohortId(team));
        teamDTO.setId(team.getId());
        teamDTO.setName(team.getName());

        return teamDTO;
    }

    @Override
    public void updateTeamFromDTO(TeamDTO teamDTO, Team team) {
        if (teamDTO == null) {
            return;
        }

        team.setSchool(mapSchoolIdToSchool(teamDTO.getSchoolId()));
        team.setCohort(mapCohortIdToCohort(teamDTO.getCohortId()));
        team.setId(teamDTO.getId());
        team.setName(teamDTO.getName());
    }

    private UUID teamSchoolId(Team team) {
        School school = team.getSchool();
        if (school == null) {
            return null;
        }
        return school.getId();
    }

    private UUID teamCohortId(Team team) {
        Cohort cohort = team.getCohort();
        if (cohort == null) {
            return null;
        }
        return cohort.getId();
    }
}
