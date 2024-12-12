package com.projecthub.mapper;

import com.projecthub.dto.AppUserDTO;
import com.projecthub.dto.RegisterRequestDTO;
import com.projecthub.model.AppUser;
import org.mapstruct.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AppUserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "encodedPassword")
    AppUser toAppUser(RegisterRequestDTO registerRequest, @Context String encodedPassword);

    @Mapping(target = "password", ignore = true)
    AppUserDTO toAppUserDTO(AppUser appUser);
}