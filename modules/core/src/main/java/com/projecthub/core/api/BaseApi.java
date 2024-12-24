package com.projecthub.core.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@ApiResponses(value = {
    @ApiResponse(responseCode = "500", description = "Internal server error"),
    @ApiResponse(responseCode = "400", description = "Bad request"),
    @ApiResponse(responseCode = "404", description = "Resource not found")
})
public interface BaseApi<T, I> {
    @Operation(summary = "Get resource by ID")
    @GetMapping("/{id}")
    ResponseEntity<T> getById(@PathVariable I id);

    @Operation(summary = "Delete resource by ID")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteById(@PathVariable I id);
}
