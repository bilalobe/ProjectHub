package com.projecthub.core.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDTO(
    UUID id,
    String message,
    LocalDateTime timestamp,
    UUID userId
) {}