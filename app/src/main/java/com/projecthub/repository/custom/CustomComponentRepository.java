package com.projecthub.repository.custom;

import java.util.List;

import com.projecthub.model.Component;

public interface CustomComponentRepository {
    List<Component> findAll();
    Component save(Component component);
    void deleteById(Long componentId);
}