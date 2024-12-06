package com.projecthub.ui.controllers.main;

import org.springframework.stereotype.Component;

import com.projecthub.dto.ProjectDTO;
import com.projecthub.ui.controllers.details.ProjectDetailsController;
import com.projecthub.ui.viewmodels.ProjectHubViewModel;
import com.projecthub.utils.ui.LoaderFactory;
import com.projecthub.utils.ui.TreeItemLoader;
import com.projecthub.utils.ui.TreeItemWrapper;

import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

@Component
public class TreeViewController {

    
    private ProjectHubViewModel viewModel;

    
    private ProjectDetailsController projectDetailsController;

    
    private LoaderFactory loaderFactory;

    @FXML
    private TreeView<TreeItemWrapper> schoolTreeView;

    @FXML
    public void initialize() {
        setupTreeView();
    }

    /**
     * Sets up the TreeView with root items and lazy loading.
     */
    private void setupTreeView() {
        TreeItem<TreeItemWrapper> rootItem = new TreeItem<>(new TreeItemWrapper("Schools", null));
        schoolTreeView.setRoot(rootItem);
        schoolTreeView.setShowRoot(true);

        // Populate TreeView from ViewModel
        for (TreeItemWrapper wrapper : viewModel.getTreeItems()) {
            TreeItem<TreeItemWrapper> treeItem = new TreeItem<>(wrapper);
            rootItem.getChildren().add(treeItem);
            setupLazyLoading(treeItem);
        }

        // Customize TreeCell to display names
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

        // Handle selection changes
        schoolTreeView.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            onItemSelected(newValue);
        });
    }

    /**
     * Sets up lazy loading for the given TreeItem.
     *
     * @param parentItem the parent TreeItem
     */
    private void setupLazyLoading(TreeItem<TreeItemWrapper> parentItem) {
        parentItem.addEventHandler(TreeItem.branchExpandedEvent(), event -> {
            TreeItem<?> source = event.getSource();
            if (source instanceof TreeItem<?> treeItem && treeItem.getValue() instanceof TreeItemWrapper wrapper) {
                TreeItem<TreeItemWrapper> safeTreeItem = new TreeItem<>(wrapper);
                if (safeTreeItem.isExpanded() && safeTreeItem.getChildren().isEmpty()) {
                    Object data = safeTreeItem.getValue().getData();
                    TreeItemLoader loader = loaderFactory.getLoader(data);
                    if (loader != null) {
                        loader.load(safeTreeItem, wrapper);
                    }
                }
            }
        });
    }

    /**
     * Handles the selection of a TreeItem.
     *
     * @param selectedItem the selected TreeItem
     */
    private void onItemSelected(TreeItem<TreeItemWrapper> selectedItem) {
        if (selectedItem == null || selectedItem.getValue() == null) {
            projectDetailsController.clearProjectDetails();
            return;
        }

        Object associatedObject = selectedItem.getValue().getData();

        if (associatedObject instanceof ProjectDTO projectSummary) {
            projectDetailsController.displayProjectDetails(projectSummary);
        } else {
            projectDetailsController.clearProjectDetails();
        }
    }
}