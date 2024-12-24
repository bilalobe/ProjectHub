package com.projecthub.core.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for comment data.
 */
public record CommentDTO(
    UUID id,
    String content,
    UUID authorId,
    UUID taskId,
    LocalDateTime timestamp
) {}