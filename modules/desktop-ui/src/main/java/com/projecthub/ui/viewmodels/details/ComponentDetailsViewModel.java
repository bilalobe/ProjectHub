package com.projecthub.ui.viewmodels.details;

import com.projecthub.core.dto.ComponentDTO;
import com.projecthub.core.services.project.ComponentService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * ViewModel for managing component details.
 */
@Component
public class ComponentDetailsViewModel {

    private static final Logger logger = LoggerFactory.getLogger(ComponentDetailsViewModel.class);

    private final ComponentService componentService;

    private final ObjectProperty<UUID> componentId = new SimpleObjectProperty<>();
    private final StringProperty componentName = new SimpleStringProperty();
    private final StringProperty componentDescription = new SimpleStringProperty();
    private final ObjectProperty<UUID> projectId = new SimpleObjectProperty<>();

    /**
     * Constructor with dependency injection.
     *
     * @param componentService the component service
     */
    public ComponentDetailsViewModel(ComponentService componentService) {
        this.componentService = componentService;
    }

    public ObjectProperty<UUID> componentIdProperty() {
        return componentId;
    }

    public StringProperty componentNameProperty() {
        return componentName;
    }

    public StringProperty componentDescriptionProperty() {
        return componentDescription;
    }

    public ObjectProperty<UUID> projectIdProperty() {
        return projectId;
    }

    /**
     * Sets the current component.
     *
     * @param component the component DTO
     */
    public void setComponent(ComponentDTO component) {
        if (component == null) {
            logger.warn("Component is null in setComponent()");
            clearComponent();
            return;
        }

        componentId.set(component.getId());
        componentName.set(component.getName());
        componentDescription.set(component.getDescription());
        projectId.set(component.getProjectId());
    }

    /**
     * Saves the component.
     *
     * @param componentSummary the component DTO
     */
    public void saveComponent(ComponentDTO componentSummary) {
        if (componentSummary == null) {
            logger.warn("ComponentSummary is null in saveComponent()");
            return;
        }
        try {
            ComponentDTO savedComponent;
            if (componentSummary.getId() != null) {
                savedComponent = componentService.updateComponent(componentSummary.getId(), componentSummary);
            } else {
                savedComponent = componentService.saveComponent(componentSummary);
            }
            setComponent(savedComponent);
        } catch (Exception e) {
            logger.error("Failed to save component: {}", componentSummary, e);
        }
    }

    /**
     * Deletes the component by ID.
     *
     * @param componentId the component ID
     */
    public void deleteComponent(UUID componentId) {
        if (componentId == null) {
            logger.warn("ComponentId is null in deleteComponent()");
            return;
        }
        try {
            componentService.deleteComponent(componentId);
            clearComponent();
        } catch (Exception e) {
            logger.error("Failed to delete component ID: {}", componentId, e);
        }
    }

    /**
     * Clears the current component data.
     */
    public void clearComponent() {
        componentId.set(null);
        componentName.set("");
        componentDescription.set("");
        projectId.set(null);
    }
}