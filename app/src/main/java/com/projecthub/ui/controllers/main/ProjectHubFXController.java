package com.projecthub.ui.controllers.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.utils.ui.LoaderFactory;
import com.projecthub.utils.ui.TreeItemWrapper;
import com.projecthub.ui.viewmodels.ProjectHubViewModel;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

@Component
public class ProjectHubFXController {

    
    private ProjectHubViewModel viewModel;

    @Autowired
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

        schoolTreeView.setCellFactory(_ -> new TreeCell<TreeItemWrapper>() {
            @Override
            protected void updateItem(TreeItemWrapper item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getName() == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
    }

    private TreeItem<TreeItemWrapper> createTreeItem(TreeItemWrapper wrapper) {
        TreeItem<TreeItemWrapper> treeItem = new TreeItem<>(wrapper);
        treeItem.setExpanded(false);

        // Lazy loading mechanism
        treeItem.addEventHandler(TreeItem.branchExpandedEvent(), _ -> {
            if (treeItem.getChildren().isEmpty()) {
                loadChildren(treeItem, wrapper);
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

    /**
     * Shows an alert with the specified title and message.
     *
     * @param title   the title of the alert
     * @param message the message of the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}