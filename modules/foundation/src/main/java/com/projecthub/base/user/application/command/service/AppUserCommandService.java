package com.projecthub.base.user.application.command.service;

import com.projecthub.base.user.api.dto.AppUserDTO;
import com.projecthub.base.user.api.dto.CreateUserDTO;

import java.util.UUID;

public interface AppUserCommandService {
    AppUserDTO createUser(CreateUserDTO createUserDTO);

    AppUserDTO updateUser(UUID id, AppUserDTO userDTO);

    void deleteUser(UUID id);
}
