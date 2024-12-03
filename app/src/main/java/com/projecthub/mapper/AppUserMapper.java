package com.projecthub.mapper;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.model.AppUser;
import com.projecthub.model.Team;
import org.springframework.stereotype.Component;

@Component
public class AppUserMapper {

    public AppUser toAppUser(AppUserSummary userSummary, Team team, String encodedPassword) {
        AppUser user = new AppUser();
        // auto ID setting
        user.setUsername(userSummary.getUsername());
        user.setEmail(userSummary.getEmail());
        user.setPassword(encodedPassword);
        user.setTeam(team);
        user.setFirstName(userSummary.getFirstName());
        user.setLastName(userSummary.getLastName());
        return user;
    }

    public AppUserSummary toAppUserSummary(AppUser user) {
        return new AppUserSummary(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getTeam() != null ? user.getTeam().getId() : null
        );
    }
}