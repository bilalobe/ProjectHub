package com.projecthub.core.mappers;

import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.dto.RegisterRequestDTO;
import com.projecthub.core.entities.AppUser;
import org.mapstruct.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AppUserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "encodedPassword")
    AppUser toAppUser(RegisterRequestDTO registerRequest, @Context String encodedPassword);

    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet()))")
    @Mapping(target = "password", ignore = true)
    AppUserDTO toAppUserDTO(AppUser user);

    @Named("toAppUserSummary")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    AppUserDTO toAppUserSummary(AppUser user);
}