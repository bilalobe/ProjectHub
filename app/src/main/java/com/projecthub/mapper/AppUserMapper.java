package com.projecthub.mapper;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.model.AppUser;
import com.projecthub.model.Team;

public class AppUserMapper {

    public static AppUser toAppUser(AppUserSummary userSummary, Team team) {
        AppUser user = new AppUser();
        user.setId(userSummary.getId());
        user.setUsername(userSummary.getUsername());
        user.setPassword(userSummary.getPassword()); // Assuming password is handled securely
        user.setTeam(team);
        return user;
    }

    public static AppUserSummary toAppUserSummary(AppUser user) {
        return new AppUserSummary(
            user.getId(),
            user.getUsername(),
            user.getPassword(), // Assuming password is handled securely
            user.getTeam() != null ? user.getTeam().getId() : null
        );
    }
}