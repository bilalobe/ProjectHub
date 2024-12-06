package com.projecthub.ui.controllers.main;

import org.springframework.stereotype.Component;

import com.projecthub.dto.ProjectDTO;
import com.projecthub.ui.viewmodels.ProjectHubViewModel;
import com.projecthub.utils.ui.LoaderFactory;
import com.projecthub.utils.ui.TreeItemLoader;
import com.projecthub.utils.ui.TreeItemWrapper;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

@Component
public class TreeViewController {


    private ProjectHubViewModel viewModel;


    private LoaderFactory loaderFactory;

    @FXML
    private TreeView<TreeItemWrapper> schoolTreeView;

    @FXML
    private Label projectNameLabel;

    @FXML
    private TextArea projectDescriptionTextArea;

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
        schoolTreeView.setCellFactory(this::createTreeCell);

        // Handle selection changes
        schoolTreeView.getSelectionModel().selectedItemProperty().addListener(this::handleSelectionChange);
    }

    /**
     * Creates a TreeCell for displaying TreeItemWrapper names.
     *
     * @param treeView the TreeView
     * @return the TreeCell
     */
    private TreeCell<TreeItemWrapper> createTreeCell(TreeView<TreeItemWrapper> treeView) {
        return new TreeCell<TreeItemWrapper>() {
            @Override
            protected void updateItem(TreeItemWrapper item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getName() == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        };
    }

    /**
     * Sets up lazy loading for the given TreeItem.
     *
     * @param parentItem the parent TreeItem
     */
    private void setupLazyLoading(TreeItem<TreeItemWrapper> parentItem) {
        parentItem.addEventHandler(TreeItem.branchExpandedEvent(), this::handleBranchExpanded);
    }

    /**
     * Handles the branch expanded event for lazy loading.
     *
     * @param event the branch expanded event
     */
    private void handleBranchExpanded(TreeItem.TreeModificationEvent<TreeItemWrapper> event) {
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
    }

    /**
     * Handles the selection change in the TreeView.
     *
     * @param observable the observable value
     * @param oldValue   the old selected item
     * @param newValue   the new selected item
     */
    private void handleSelectionChange(ObservableValue<? extends TreeItem<TreeItemWrapper>> observable,
                                       TreeItem<TreeItemWrapper> oldValue,
                                       TreeItem<TreeItemWrapper> newValue) {
        onItemSelected(newValue != null ? newValue.getValue() : null);
    }

    /**
     * Handles the selection of a TreeItem.
     *
     * @param selectedItem the selected TreeItemWrapper
     */
    private void onItemSelected(TreeItemWrapper selectedItem) {
        if (selectedItem == null || selectedItem.getData() == null) {
            clearProjectDetails();
            return;
        }

        Object associatedObject = selectedItem.getData();

        if (associatedObject instanceof ProjectDTO projectSummary) {
            displayProjectDetails(projectSummary);
        } else {
            clearProjectDetails();
        }
    }

    /**
     * Displays the details of the selected project.
     *
     * @param projectSummary the selected ProjectDTO
     */
    private void displayProjectDetails(ProjectDTO projectSummary) {
        // Update UI components to show the project details
        projectNameLabel.setText(projectSummary.getName());
        projectDescriptionTextArea.setText(projectSummary.getDescription());
    }

    /**
     * Clears the project details from the UI.
     */
    private void clearProjectDetails() {
        // Clear the UI components
        projectNameLabel.setText("");
        projectDescriptionTextArea.setText("");
    }
}