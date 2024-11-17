package com.projecthub.utils.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;

@Component
public class SetupLazyLoading {

    @Autowired
    private PopulatorUtility populatorUtility;

    /**
     * Sets up lazy loading for the given parent TreeItem.
     *
     * @param parentItem the parent TreeItem to add lazy loading to
     * @param parentId   the ID associated with the parent item
     * @param level      the level of the tree hierarchy (e.g., "School", "Class")
     */
    public void setupLazyLoading(TreeItem<String> parentItem, Long parentId, String level) {
        parentItem.addEventHandler(TreeItem.branchExpandedEvent(), (TreeModificationEvent<String> event) -> {
            TreeItem<String> source = event.getSource();
            if (source.getChildren().isEmpty()) {
                switch (level) {
                    case "School" -> populatorUtility.populateCohorts(source, parentId);
                    case "Class" -> populatorUtility.populateTeams(source, parentId);
                    case "Team" -> populatorUtility.populateProjects(source, parentId);
                    case "Project" -> populatorUtility.populateComponents(source, parentId);
                    default -> {
                    }
                }
                // Optional: Handle unexpected levels
                            }
        });
    }
}