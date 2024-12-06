package com.projecthub.utils.ui;

import org.springframework.stereotype.Component;

import java.util.UUID;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SetupLazyLoading {

    private static final Logger logger = LoggerFactory.getLogger(SetupLazyLoading.class);

    
    private PopulatorUtility populatorUtility;

    /**
     * Sets up lazy loading for the given parent TreeItem.
     *
     * @param parentItem the parent TreeItem to add lazy loading to
     * @param parentId   the ID associated with the parent item
     * @param level      the level of the tree hierarchy (e.g., "School", "Cohort", "Team", "Project")
     */
    public void setupLazyLoading(TreeItem<TreeItemWrapper> parentItem, UUID parentId, String level) {
        parentItem.addEventHandler(TreeItem.branchExpandedEvent(), (TreeModificationEvent<TreeItemWrapper> event) -> {
            TreeItem<TreeItemWrapper> source = event.getSource();
            if (source.getChildren().isEmpty()) {
                logger.info("Expanding {} with ID {}", level, parentId);
                switch (level) {
                    case "School" -> populatorUtility.populateCohorts(source, parentId);
                    case "Cohort" -> populatorUtility.populateTeams(source, parentId);
                    case "Team" -> populatorUtility.populateProjects(source, parentId);
                    case "Project" -> populatorUtility.populateComponents(source, parentId);
                    default -> logger.warn("Unexpected level: {}", level);
                }
            }
        });
    }

    /**
     * Sets up lazy loading for multiple levels of the tree hierarchy.
     *
     * @param parentItem the parent TreeItem to add lazy loading to
     * @param parentId   the ID associated with the parent item
     * @param levels     the levels of the tree hierarchy (e.g., "School", "Cohort", "Team", "Project")
     */
    public void setupLazyLoading(TreeItem<TreeItemWrapper> parentItem, UUID parentId, String... levels) {
        for (String level : levels) {
            setupLazyLoading(parentItem, parentId, level);
        }
    }
}