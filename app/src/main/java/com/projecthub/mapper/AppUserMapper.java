package com.projecthub.mapper;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.model.AppUser;
import com.projecthub.model.Team;
import com.projecthub.service.PasswordService;

public class AppUserMapper {

    public static AppUser toAppUser(AppUserSummary userSummary, Team team, PasswordService passwordService, String password) {
        AppUser user = new AppUser();
        user.setId(userSummary.getId());
        user.setUsername(userSummary.getUsername());
        if (password != null && !password.isEmpty()) {
            String encodedPassword = passwordService.encodePassword(password);
            user.setPassword(encodedPassword);
        }
        user.setTeam(team);
        return user;
    }

    public static AppUserSummary toAppUserSummary(AppUser user) {
        return new AppUserSummary(
            user.getId(),
            user.getUsername(), user.getPassword(),
            user.getTeam() != null ? user.getTeam().getId() : null
        );
    }
}