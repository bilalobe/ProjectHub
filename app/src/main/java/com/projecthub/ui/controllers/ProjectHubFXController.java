package com.projecthub.ui.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import com.projecthub.utils.PopulatorUtility;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

@org.springframework.stereotype.Component
public class ProjectHubFXController<School> {

    @Autowired
    private PopulatorUtility populatorUtility;

    @FXML
    private TreeView<String> schoolTreeView;

    @FXML
    private TextField searchField;

    private TreeItem<String> rootItem;

    @FXML
    public void initialize() {
        loadSchools();
        setupSearchFilter();
    }

    private void loadSchools() {
        rootItem = new TreeItem<>("Schools");
        rootItem.setExpanded(true);
        schoolTreeView.setRoot(rootItem);
        populatorUtility.populateSchools(rootItem);
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            schoolTreeView.setRoot(buildFilteredTree(newValue.toLowerCase()));
        });
    }

    private TreeItem<String> buildFilteredTree(String filter) {
        TreeItem<String> filteredRoot = new TreeItem<>("Schools");
        filteredRoot.setExpanded(true);

        

        for (TreeItem<String> school : rootItem.getChildren()) {
            if (school.getValue().toLowerCase().contains(filter)) {
                filteredRoot.getChildren().add(school);
            }
        }

        return filteredRoot;
    }
}