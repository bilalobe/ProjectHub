package com.projecthub.base.auth.domain.token;

import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.Value;
import lombok.With;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@With
@FieldNameConstants
public class AuthenticationToken {
    UUID id;
    UUID userId;
    String token;
    LocalDateTime expiresAt;

    @SneakyThrows
    @Synchronized
    public static AuthenticationToken generate(final UUID userId) {
        // Thread-safe token generation
        return new AuthenticationToken(
            UUID.randomUUID(),
            userId,
            UUID.randomUUID().toString(),
            LocalDateTime.now().plusDays(1)
        );
    }
}
