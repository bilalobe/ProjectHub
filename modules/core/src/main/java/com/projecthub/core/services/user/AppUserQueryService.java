package com.projecthub.core.services.user;

import com.projecthub.core.dto.AppUserDTO;
import java.util.List;
import java.util.UUID;

public interface AppUserQueryService {
    List<AppUserDTO> getAllUsers();

    AppUserDTO getUserById(UUID id);
}
