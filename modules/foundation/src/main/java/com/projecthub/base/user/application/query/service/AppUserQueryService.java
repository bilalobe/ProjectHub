package com.projecthub.base.user.application.query.service;

import com.projecthub.base.user.api.dto.AppUserDTO;

import java.util.List;
import java.util.UUID;

public interface AppUserQueryService {
    List<AppUserDTO> getAllUsers();

    AppUserDTO getUserById(UUID id);
}
