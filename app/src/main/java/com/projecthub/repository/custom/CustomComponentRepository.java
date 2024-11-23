package com.projecthub.repository.custom;

import java.util.List;

import com.projecthub.dto.ComponentSummary;
import com.projecthub.model.Component;

public interface CustomComponentRepository {
    List<Component> findAll();
    ComponentSummary save(ComponentSummary componentSummary);
    void deleteById(Long componentId);
    List<Component> findByProjectId(Long projectId);

    public Object findById(Long id);
}