package com.projecthub.base.auth.domain.mapper;


import com.projecthub.base.auth.domain.entity.Role;
import com.projecthub.base.shared.api.mapper.BaseMapper;
import org.mapstruct.*;

/**
 * Mapper for Role entity with protected name field handling.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper extends BaseMapper<RoleDTO, Role> {

    @Override
    @Mapping(target = "id", ignore = true)
    Role toEntity(RoleDTO dto);

    @Override
    RoleDTO toDto(Role entity);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    void updateEntityFromDto(RoleDTO dto, @MappingTarget Role entity);
}
