package com.projecthub.core.dto;

import com.projecthub.core.enums.RoleType;
import java.util.UUID;

public record RoleDTO(
    UUID id,
    RoleType name
) {}