package com.projecthub.core.services.user;

import com.projecthub.core.dto.AppUserProfileDTO;
import com.projecthub.core.dto.UpdateUserRequestDTO;
import java.util.UUID;

public interface AppUserProfileService {
    AppUserProfileDTO getUserProfileById(UUID id);
    AppUserProfileDTO updateUserProfile(UUID id, UpdateUserRequestDTO updateRequest);
}
