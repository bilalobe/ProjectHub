package com.projecthub.base.auth.application.registration;


import com.projecthub.base.auth.api.dto.RegisterRequestDTO;
import com.projecthub.base.user.api.dto.AppUserDTO;

public interface UserRegistration {
    AppUserDTO registerUser(RegisterRequestDTO request);

    void verifyEmail(String token);

    void resendVerification(String email);
}
