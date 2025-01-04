package com.projecthub.base.user.api.mapper;

import com.projecthub.base.shared.api.mapper.BaseMapper;
import com.projecthub.base.user.api.dto.AppUserProfileDTO;
import com.projecthub.base.user.api.dto.UpdateUserRequestDTO;
import com.projecthub.base.user.domain.entity.AppUser;
import org.mapstruct.*;

/**
 * Mapper interface for converting between AppUser and AppUserProfileDTO objects.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppUserProfileMapper extends BaseMapper<AppUserProfileDTO, AppUser> {

    AppUserProfileDTO toDto(AppUser entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(AppUserProfileDTO dto, @MappingTarget AppUser entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateUserRequestDTO request, @MappingTarget AppUser entity);
}
