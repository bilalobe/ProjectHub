package com.projecthub.core.mappers;

import com.projecthub.core.dto.AppUserProfileDTO;
import com.projecthub.core.dto.UpdateUserRequestDTO;
import com.projecthub.core.entities.AppUser;
import org.mapstruct.*;

/**
 * Mapper interface for converting between AppUser and AppUserProfileDTO objects.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppUserProfileMapper extends BaseMapper<AppUserProfileDTO, AppUser> {

    @Override
    AppUserProfileDTO toDto(AppUser entity);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(AppUserProfileDTO dto, @MappingTarget AppUser entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateUserRequestDTO request, @MappingTarget AppUser entity);
}