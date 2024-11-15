package com.projecthub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projecthub.model.Component;
import com.projecthub.repository.custom.CustomComponentRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Service
@Api(value = "Component Service", description = "Operations pertaining to components in ProjectHub")
public class ComponentService {

    private final CustomComponentRepository componentRepository;

    public ComponentService(@Qualifier("csvComponentRepository") CustomComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    @ApiOperation(value = "View a list of all components", response = List.class)
    public List<Component> getAllComponents() {
        return componentRepository.findAll();
    }

    @ApiOperation(value = "Save a component")
    public Component saveComponent(Component component) {
        return componentRepository.save(component);
    }

    @ApiOperation(value = "Delete a component by ID")
    public void deleteComponent(Long id) {
        componentRepository.deleteById(id);
    }
}