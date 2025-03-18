package com.projecthub.core.api;

public interface BackwardCompatible {
    boolean isCompatibleWith(String previousVersion);
    String[] getBreakingChanges();
    String getMigrationPath();
}