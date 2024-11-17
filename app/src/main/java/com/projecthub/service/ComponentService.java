package com.projecthub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projecthub.model.Component;
import com.projecthub.repository.custom.CustomComponentRepository;

@Service
public class ComponentService {

    private final CustomComponentRepository componentRepository;

    public ComponentService(@Qualifier("csvComponentRepository") CustomComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    public List<Component> getAllComponents() {
        return componentRepository.findAll();
    }

    public Component saveComponent(Component component) {
        return componentRepository.save(component);
    }

    public void deleteComponent(Long id) {
        componentRepository.deleteById(id);
    }

    public List<Component> getComponentsByProjectId(Long projectId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}