package com.projecthub.mapper;

import com.projecthub.dto.AppUserDTO;
import com.projecthub.model.AppUser;
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
public class AppUserMapperImpl implements AppUserMapper {

    @Override
    public AppUserDTO toAppUserDTO(AppUser user) {
        if (user == null) {
            return null;
        }

        AppUserDTO appUserDTO = new AppUserDTO();

        appUserDTO.setTeamId(userTeamId(user));
        appUserDTO.setId(user.getId());
        appUserDTO.setUsername(user.getUsername());
        appUserDTO.setEmail(user.getEmail());
        appUserDTO.setFirstName(user.getFirstName());
        appUserDTO.setLastName(user.getLastName());

        return appUserDTO;
    }

    private UUID userTeamId(AppUser appUser) {
        Team team = appUser.getTeam();
        if (team == null) {
            return null;
        }
        return team.getId();
    }
}
