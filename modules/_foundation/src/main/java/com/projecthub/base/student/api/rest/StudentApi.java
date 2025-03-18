package com.projecthub.base.student.api.rest;

import com.projecthub.base.shared.api.rest.BaseApi;
import com.projecthub.base.student.api.dto.StudentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Tag(name = "Students", description = "Student management API")
@RequestMapping("/api/v1/students")
public interface StudentApi extends BaseApi<StudentDTO, UUID> {

    @Operation(summary = "Get all students",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved students")
        })
    @GetMapping
    ResponseEntity<List<StudentDTO>> getAllStudents();

    @Operation(summary = "Get students by team ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved students for team")
        })
    @GetMapping("/team/{teamId}")
    ResponseEntity<List<StudentDTO>> getStudentsByTeamId(@PathVariable UUID teamId);

    @Operation(summary = "Assign student to team",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully assigned student to team")
        })
    @PostMapping("/{studentId}/team/{teamId}")
    ResponseEntity<StudentDTO> assignToTeam(
        @PathVariable UUID studentId,
        @PathVariable UUID teamId);
}
