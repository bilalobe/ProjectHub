package com.projecthub.base.submission.domain.value;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private UUID commentId;
    private String text;
    private UUID authorId;
    private Instant createdAt;
}