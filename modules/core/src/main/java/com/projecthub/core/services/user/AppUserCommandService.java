package com.projecthub.core.services.user;

import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.dto.CreateUserDTO;

import java.util.UUID;

public interface AppUserCommandService {
    AppUserDTO createUser(CreateUserDTO createUserDTO);

    AppUserDTO updateUser(UUID id, AppUserDTO userDTO);

    void deleteUser(UUID id);
}