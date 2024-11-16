package com.projecthub.ui.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.model.School;
import com.projecthub.ui.viewmodels.ProjectHubViewModel;
import com.projecthub.utils.ui.TreeItemWrapper;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

@Component
public class ProjectHubFXController {

    @Autowired
    private ProjectHubViewModel viewModel;

    @FXML
    private TreeView<String> schoolTreeView;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        bindProperties();
        setupTreeView();
    }

    private void bindProperties() {
        searchField.textProperty().bindBidirectional(viewModel.searchQueryProperty());
    }

    private void setupTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("Schools");
        schoolTreeView.setRoot(rootItem);
        schoolTreeView.setShowRoot(true);

        for (TreeItemWrapper wrapper : viewModel.getTreeItems()) {
            TreeItem<String> treeItem = createTreeItem(wrapper);
            rootItem.getChildren().add(treeItem);
        }

        schoolTreeView.setCellFactory((TreeView<String> param) -> new TextFieldTreeCellImpl());
    }

    private TreeItem<String> createTreeItem(TreeItemWrapper wrapper) {
        TreeItem<String> treeItem = new TreeItem<>(wrapper.getName());
        treeItem.setExpanded(false);

        // Lazy loading mechanism
        treeItem.addEventHandler(TreeItem.branchExpandedEvent(), event -> {
            if (treeItem.getChildren().isEmpty()) {
                loadChildren(treeItem, wrapper);
            }
        });

        return treeItem;
    }

    private void loadChildren(TreeItem<String> parentItem, TreeItemWrapper parentWrapper) {
        Object data = parentWrapper.getData();

        if (data instanceof School school) {
            List<com.projecthub.model.Class> classes = viewModel.getClassesBySchoolId(school.getId());
            for (com.projecthub.model.Class cls : classes) {
                var classWrapper = new TreeItemWrapper(cls.getName(), cls);
                TreeItem<String> classItem = createTreeItem(classWrapper);
                parentItem.getChildren().add(classItem);
            }
        }
        // Similar logic for Class, Team, Project, Component
    }

    // Custom TreeCell to handle editing or custom display
    private static class TextFieldTreeCellImpl extends TreeCell<String> {
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty ? null : item);
        }
    }
}