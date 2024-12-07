package com.projecthub.ui.controllers.main;

import org.springframework.stereotype.Component;

import com.projecthub.utils.ui.LoaderFactory;
import com.projecthub.utils.ui.TreeItemWrapper;
import com.projecthub.ui.viewmodels.ProjectHubViewModel;
import com.projecthub.utils.ui.TreeCellFactory;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem.TreeModificationEvent;

@Component
public class ProjectHubFXController {

    private ProjectHubViewModel viewModel;
    private LoaderFactory loaderFactory;

    @FXML
    private TreeView<TreeItemWrapper> schoolTreeView;

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
        TreeItem<TreeItemWrapper> rootItem = new TreeItem<>(new TreeItemWrapper("Schools", null));
        schoolTreeView.setRoot(rootItem);
        schoolTreeView.setShowRoot(true);

        for (TreeItemWrapper wrapper : viewModel.getTreeItems()) {
            TreeItem<TreeItemWrapper> treeItem = createTreeItem(wrapper);
            rootItem.getChildren().add(treeItem);
        }

        schoolTreeView.setCellFactory(TreeCellFactory::createTreeCell);
    }

    private TreeItem<TreeItemWrapper> createTreeItem(TreeItemWrapper wrapper) {
        TreeItem<TreeItemWrapper> treeItem = new TreeItem<>(wrapper);
        treeItem.setExpanded(false);

        // Lazy loading mechanism
        treeItem.addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler<TreeModificationEvent<TreeItemWrapper>>() {
            @Override
            public void handle(TreeModificationEvent<TreeItemWrapper> event) {
                if (treeItem.getChildren().isEmpty()) {
                    loadChildren(treeItem, wrapper);
                }
            }
        });

        return treeItem;
    }

    private void loadChildren(TreeItem<TreeItemWrapper> treeItem, TreeItemWrapper parentWrapper) {
        try {
            loaderFactory.getLoader(parentWrapper.getData()).load(treeItem, parentWrapper);
        } catch (Exception e) {
            showAlert("Error", "Failed to load children: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}