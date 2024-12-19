package com.projecthub.core.utils;

import javafx.util.StringConverter;

import java.util.UUID;

public class UUIDStringConverter extends StringConverter<UUID> {

    @Override
    public String toString(UUID uuid) {
        return uuid != null ? uuid.toString() : "";
    }

    @Override
    public UUID fromString(String string) {
        try {
            return string != null && !string.isEmpty() ? UUID.fromString(string) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}