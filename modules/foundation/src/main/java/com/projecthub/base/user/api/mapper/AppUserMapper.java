package com.projecthub.base.user.api.mapper;

import com.projecthub.base.auth.api.dto.RegisterRequestDTO;
import com.projecthub.base.shared.api.mapper.BaseMapper;
import com.projecthub.base.user.api.dto.AppUserDTO;
import com.projecthub.base.user.domain.entity.AppUser;
import org.mapstruct.*;

/**
 * Mapper for AppUser entity with protected password handling.
 */
@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppUserMapper extends BaseMapper<AppUserDTO, AppUser> {

    @Override
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))")
    AppUserDTO toDto(AppUser user);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    AppUser toEntity(AppUserDTO dto);

    @Named("registrationMapping")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "roles", ignore = true)
    AppUser toEntity(RegisterRequestDTO dto, @Context String encodedPassword);

    @Named("summary")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    AppUserDTO toSummaryDto(AppUser entity);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateEntityFromDto(AppUserDTO dto, @MappingTarget AppUser entity);
}
