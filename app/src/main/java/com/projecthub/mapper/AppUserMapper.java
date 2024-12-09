package com.projecthub.mapper;

import com.projecthub.dto.AppUserDTO;
import com.projecthub.model.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    @Mapping(target = "password", ignore = true)
    AppUserDTO toAppUserDTO(AppUser user);

    @Mapping(target = "password", source = "encodedPassword")
    AppUser toAppUser(AppUserDTO userDTO, String encodedPassword);

    @Mapping(target = "password", source = "encodedPassword")
    void updateAppUserFromDTO(AppUserDTO userDTO, @MappingTarget AppUser user, String encodedPassword);
}