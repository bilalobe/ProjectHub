package com.projecthub.core.mappers;

import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.dto.RegisterRequestDTO;
import com.projecthub.core.models.AppUser;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AppUserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "encodedPassword")
    AppUser toAppUser(RegisterRequestDTO registerRequest, @Context String encodedPassword);

    @Mapping(target = "password", ignore = true)
    AppUserDTO toAppUserDTO(AppUser appUser);
}