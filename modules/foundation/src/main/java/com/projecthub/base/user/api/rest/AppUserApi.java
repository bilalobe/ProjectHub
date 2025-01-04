package com.projecthub.base.users.api.rest;

import com.projecthub.base.auth.api.dto.RegisterRequestDTO;
import com.projecthub.base.shared.api.rest.BaseApi;
import com.projecthub.base.users.api.dto.AppUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "User API", description = "Operations for managing users")
public interface AppUserApi extends BaseApi<AppUserDTO, UUID> {

    @Operation(summary = "Get all users")
    ResponseEntity<List<AppUserDTO>> getAllUsers();

    @Operation(summary = "Create a new user")
    ResponseEntity<AppUserDTO> createUser(RegisterRequestDTO registerRequest);
}
