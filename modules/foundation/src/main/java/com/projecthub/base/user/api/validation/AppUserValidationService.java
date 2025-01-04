package com.projecthub.base.user.api.validation;

import com.projecthub.base.auth.api.dto.RegisterRequestDTO;
import com.projecthub.base.user.api.dto.UpdateUserRequestDTO;

public interface AppUserValidationService {
    void validateRegistration(RegisterRequestDTO request);

    void validateUpdate(UpdateUserRequestDTO request, String currentUsername, String currentEmail);

    void validateUniqueUsername(String username);

    void validateUniqueEmail(String email);
}
