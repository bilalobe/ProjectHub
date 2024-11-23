package com.projecthub.mapper;

import com.projecthub.dto.TeamSummary;
import com.projecthub.model.Team;
import com.projecthub.model.School;

public class TeamMapper {

    public static Team toTeam(TeamSummary teamSummary, School school) {
        Team team = new Team();
        team.setId(teamSummary.getId());
        team.setName(teamSummary.getName());
        team.setSchool(school);
        return team;
    }

    public static TeamSummary toTeamSummary(Team team) {
        return new TeamSummary(
            team.getId(),
            team.getName(),
            team.getSchool() != null ? team.getSchool().getId() : null
        );
    }
}