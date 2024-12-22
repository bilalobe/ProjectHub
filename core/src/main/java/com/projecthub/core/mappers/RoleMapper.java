package com.projecthub.core.mappers;

import com.projecthub.core.dto.RoleDTO;
import com.projecthub.core.entities.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toRoleDTO(Role role);

    Role toRole(RoleDTO roleDTO);
}