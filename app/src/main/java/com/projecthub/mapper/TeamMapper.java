package com.projecthub.mapper;

import com.projecthub.dto.TeamSummary;
import com.projecthub.model.Team;
import com.projecthub.model.School;
import com.projecthub.model.Cohort;

public class TeamMapper {

    public static Team toTeam(TeamSummary teamSummary, School school, Cohort cohort) {
        Team team = new Team();
        team.setId(teamSummary.getId());
        team.setName(teamSummary.getName());
        team.setSchool(school);
        team.setCohort(cohort);
        return team;
    }

    public static TeamSummary toTeamSummary(Team team) {
        return new TeamSummary(
            team.getId(),
            team.getName(),
            team.getSchool() != null ? team.getSchool().getId() : null,
            team.getCohort() != null ? team.getCohort().getId() : null
        );
    }
}