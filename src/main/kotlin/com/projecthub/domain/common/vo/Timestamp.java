package com.projecthub.core.domain.common.valueobject;

import java.time.Instant;

public class Timestamp {
    private final Instant value;

    private Timestamp(Instant value) {
        this.value = value;
    }

    public static Timestamp now() {
        return new Timestamp(Instant.now());
    }

    public static Timestamp from(Instant instant) {
        return new Timestamp(instant);
    }

    public Instant getValue() {
        return value;
    }
}
