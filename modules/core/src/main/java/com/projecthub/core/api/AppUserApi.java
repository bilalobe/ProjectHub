package com.projecthub.core.api;

import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.dto.RegisterRequestDTO;
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