package com.projecthub.mapper;

import com.projecthub.dto.AppUserDTO;
import com.projecthub.model.AppUser;
import com.projecthub.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    @Mapping(source = "team.id", target = "teamId")
    AppUserDTO toAppUserDTO(AppUser user);

    default AppUser toAppUser(AppUserDTO userDTO, Team team, String encodedPassword) {
        if (userDTO == null) {
            return null;
        }
        AppUser user = new AppUser();
        user.setUsername(userDTO.getUsername());
        user.setPassword(encodedPassword);
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setTeam(team);
        return user;
    }

    default void updateAppUserFromDTO(AppUserDTO userDTO, @MappingTarget AppUser user, Team team, String encodedPassword) {
        if (userDTO == null || user == null) {
            return;
        }
        user.setUsername(userDTO.getUsername());
        user.setPassword(encodedPassword);
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setTeam(team);
    }
}