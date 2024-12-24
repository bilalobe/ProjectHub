package com.projecthub.core.services.registration;


import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.dto.RegisterRequestDTO;

public interface UserRegistration {
    AppUserDTO registerUser(RegisterRequestDTO request);
    void verifyEmail(String token);
    void resendVerification(String email);
}
