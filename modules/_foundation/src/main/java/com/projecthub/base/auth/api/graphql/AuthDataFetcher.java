package com.projecthub.base.auth.api.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import com.projecthub.base.auth.api.dto.*;
import com.projecthub.base.auth.application.registration.AppUserRegistrationService;
import com.projecthub.base.auth.domain.event.AuthEventPublisher;
import com.projecthub.base.auth.service.security.AuthenticationService;
import com.projecthub.base.user.api.dto.AppUserCredentialsDTO;
import com.projecthub.base.user.api.dto.AppUserDTO;
import lombok.RequiredArgsConstructor;

@DgsComponent
@RequiredArgsConstructor
public class AuthDataFetcher {

    private final AuthenticationService authService;
    private final AppUserRegistrationService registrationService;
    private final AuthEventPublisher eventPublisher;

    @DgsMutation
    public AuthResponseDTO login(@InputArgument final String username, @InputArgument final String password) {
        final LoginRequestDTO request = new LoginRequestDTO(
            username,
            password,
            false,
            null
        );

        final AuthenticationResultDTO result = this.authService.authenticate(request);
        this.eventPublisher.publishUserLoggedIn(result.userId());

        final AppUserDTO user = AuthenticationService.getCurrentUser();
        return new AuthResponseDTO(result.token(), user);
    }

    @DgsMutation
    public AppUserDTO register(@InputArgument final RegisterInput input) {
        final AppUserCredentialsDTO credentials = new AppUserCredentialsDTO(
            input.username(),
            input.password()
        );

        final RegisterRequestDTO request = new RegisterRequestDTO(
            credentials,
            input.email(),
            input.username()
        );

        final AppUserDTO user = this.registrationService.registerUser(request);
        this.eventPublisher.publishUserRegistered(user.id());
        return user;
    }
}
