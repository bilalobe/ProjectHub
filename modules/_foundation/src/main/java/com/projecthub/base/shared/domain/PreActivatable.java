package com.projecthub.base.shared.domain;

public interface PreActivatable {
    void activate();
    void deactivate();
    boolean isActive();
}