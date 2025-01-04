package com.projecthub.ui.modules.layout;

import com.projecthub.ui.shared.utils.LoaderFactory;
import com.projecthub.ui.shared.utils.TreeCellFactory;
import com.projecthub.ui.shared.utils.TreeItemWrapper;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeView;
import org.springframework.stereotype.Component;

@Component
public class ProjectHubFXController {

    private final ProjectHubViewModel viewModel;
    private final LoaderFactory loaderFactory;

    @FXML
    private TreeView<TreeItemWrapper> schoolTreeView;

    @FXML
    private TextField searchField;

    public ProjectHubFXController(ProjectHubViewModel viewModel, LoaderFactory loaderFactory) {
        this.viewModel = viewModel;
        this.loaderFactory = loaderFactory;
    }

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
        treeItem.addEventHandler(TreeItem.branchExpandedEvent(),
            new EventHandler<TreeModificationEvent<TreeItemWrapper>>() {
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
