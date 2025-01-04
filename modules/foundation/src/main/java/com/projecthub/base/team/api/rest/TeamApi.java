package com.projecthub.base.team.api.rest;

import com.projecthub.base.shared.api.rest.BaseApi;
import com.projecthub.base.team.api.dto.TeamDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Teams", description = "Team management API")
@RequestMapping("/api/v1/teams")
public interface TeamApi extends BaseApi<TeamDTO, UUID> {

    @Operation(summary = "Get all teams",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved teams")
        })
    @GetMapping
    ResponseEntity<List<TeamDTO>> getAllTeams();

    @Operation(summary = "Create a new team",
        responses = {
            @ApiResponse(responseCode = "201", description = "Team created successfully")
        })
    @PostMapping
    ResponseEntity<TeamDTO> createTeam(@Valid @RequestBody TeamDTO team);

    @Operation(summary = "Add user to team",
        responses = {
            @ApiResponse(responseCode = "200", description = "User added successfully")
        })
    @PostMapping("/{teamId}/users/{userId}")
    ResponseEntity<TeamDTO> addUserToTeam(
        @PathVariable UUID teamId,
        @PathVariable UUID userId);
}
