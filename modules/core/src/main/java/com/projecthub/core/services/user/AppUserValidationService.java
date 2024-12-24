package com.projecthub.core.services.user;

import com.projecthub.core.dto.RegisterRequestDTO;
import com.projecthub.core.dto.UpdateUserRequestDTO;

public interface AppUserValidationService {
    void validateRegistration(RegisterRequestDTO request);

    void validateUpdate(UpdateUserRequestDTO request, String currentUsername, String currentEmail);

    void validateUniqueUsername(String username);

    void validateUniqueEmail(String email);
}