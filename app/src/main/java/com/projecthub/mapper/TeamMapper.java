package com.projecthub.mapper;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.dto.TeamSummary;
import com.projecthub.model.AppUser;
import com.projecthub.model.Cohort;
import com.projecthub.model.School;
import com.projecthub.model.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    public Team toTeam(TeamSummary teamSummary, School school, Cohort cohort) {
        Team team = new Team();
        team.setId(teamSummary.getId());
        team.setName(teamSummary.getName());
        team.setSchool(school);
        team.setCohort(cohort);
        return team;
    }

    public TeamSummary toTeamSummary(Team team) {
        return new TeamSummary(
            team.getId(),
            team.getName(),
            team.getSchool() != null ? team.getSchool().getId() : null,
            team.getCohort() != null ? team.getCohort().getId() : null
        );
    }

    public TeamSummary toDetailedTeamSummary(Team team) {
        return new TeamSummary(
            team.getId(),
            team.getName(),
            team.getSchool() != null ? team.getSchool().getId() : null,
            team.getCohort() != null ? team.getCohort().getId() : null,
            team.getSchool() != null ? team.getSchool().getName() : null,
            team.getCohort() != null ? team.getCohort().getName() : null
        );
    }

    public Team toTeamWithDetails(TeamSummary teamSummary, School school, Cohort cohort) {
        Team team = new Team();
        team.setId(teamSummary.getId());
        team.setName(teamSummary.getName());
        team.setSchool(school);
        team.setCohort(cohort);
        if (teamSummary.getSchoolName() != null) {
            team.getSchool().setName(teamSummary.getSchoolName());
        }
        if (teamSummary.getCohortName() != null) {
            team.getCohort().setName(teamSummary.getCohortName());
        }
        return team;
    }

    public AppUserSummary toAppUserSummary(AppUser appUser) {
        return new AppUserSummary(
            appUser.getId(),
            appUser.getUsername(),
            appUser.getEmail(),
            appUser.getFirstName(),
            appUser.getLastName(),
            appUser.getTeam() != null ? appUser.getTeam().getId() : null
        );
    }

    public void updateTeamFromSummary(TeamSummary teamSummary, Team team) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}