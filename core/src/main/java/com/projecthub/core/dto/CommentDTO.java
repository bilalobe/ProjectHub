package com.projecthub.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for comment data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private UUID id;

    private String content;

    private UUID authorId;

    private UUID taskId;

    private LocalDateTime timestamp;
}