package com.projecthub.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for notification data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private UUID id;

    private String message;

    private LocalDateTime timestamp;

    private UUID userId;
}