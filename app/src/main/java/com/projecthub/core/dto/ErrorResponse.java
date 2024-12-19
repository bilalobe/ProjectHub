package com.projecthub.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private List<String> errors;

    public ErrorResponse(int status, String message, String error) {
        this.status = status;
        this.message = message;
        this.errors = List.of(error);
    }
}