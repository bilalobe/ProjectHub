package com.projecthub.base.shared.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard API error response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    /**
     * Timestamp when the error occurred.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * HTTP status code.
     */
    private HttpStatus status;

    /**
     * Error message.
     */
    private String message;

    /**
     * Detailed debug message.
     */
    private String debugMessage;

    /**
     * List of sub-errors (e.g., validation errors).
     */
    private List<ApiSubError> subErrors;

    /**
     * Builder method to create an ApiError with only status and message.
     *
     * @param status  the HTTP status
     * @param message the error message
     * @return a new ApiError instance
     */
    public static ApiError of(HttpStatus status, String message) {
        return ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(status)
            .message(message)
            .build();
    }

    /**
     * Builder method to create an ApiError with status, message, and debug message.
     *
     * @param status       the HTTP status
     * @param message      the error message
     * @param debugMessage the detailed debug message
     * @return a new ApiError instance
     */
    public static ApiError of(HttpStatus status, String message, String debugMessage) {
        return ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(status)
            .message(message)
            .debugMessage(debugMessage)
            .build();
    }
}
