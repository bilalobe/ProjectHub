package com.projecthub.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private final int status;
    private final String message;
    private final List<String> errors;

    public ErrorResponse(int status, String message, String error) {
        this.status = status;
        this.message = message;
        this.errors = List.of(error);
    }
}