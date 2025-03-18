package com.projecthub.base.user.application.profile.service;

import com.projecthub.base.user.api.dto.AppUserProfileDTO;
import com.projecthub.base.user.api.dto.UpdateUserRequestDTO;

import java.util.UUID;

public interface AppUserProfileService {
    AppUserProfileDTO getUserProfileById(UUID id);

    AppUserProfileDTO updateUserProfile(UUID id, UpdateUserRequestDTO updateRequest);
}
