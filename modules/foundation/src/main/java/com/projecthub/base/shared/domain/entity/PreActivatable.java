package com.projecthub.base.shared.domain.entity;

public interface PreActivatable {
    boolean isActive();
    void activate();
    void deactivate();
}
