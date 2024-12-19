package com.projecthub.ui.controllers.main;

import com.projecthub.core.dto.ProjectDTO;
import com.projecthub.ui.utils.LoaderFactory;
import com.projecthub.ui.utils.TreeCellFactory;
import com.projecthub.ui.utils.TreeItemLoader;
import com.projecthub.ui.utils.TreeItemWrapper;
import com.projecthub.ui.viewmodels.ProjectHubViewModel;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.springframework.stereotype.Component;

@Component
public class TreeViewController {

    private final ProjectHubViewModel viewModel;
    private final LoaderFactory loaderFactory;

    @FXML
    private TreeView<TreeItemWrapper> schoolTreeView;

    @FXML
    private Label projectNameLabel;

    @FXML
    private TextArea projectDescriptionTextArea;

    public TreeViewController(ProjectHubViewModel viewModel, LoaderFactory loaderFactory) {
        this.viewModel = viewModel;
        this.loaderFactory = loaderFactory;
    }

    @FXML
    public void initialize() {
        setupTreeView();
    }

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
        schoolTreeView.setCellFactory(TreeCellFactory::createTreeCell);

        // Handle selection changes
        schoolTreeView.getSelectionModel().selectedItemProperty().addListener(this::handleSelectionChange);
    }

    private void setupLazyLoading(TreeItem<TreeItemWrapper> parentItem) {
        parentItem.addEventHandler(TreeItem.branchExpandedEvent(), this::handleBranchExpanded);
    }

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

    private void handleSelectionChange(ObservableValue<? extends TreeItem<TreeItemWrapper>> observable,
                                       TreeItem<TreeItemWrapper> oldValue,
                                       TreeItem<TreeItemWrapper> newValue) {
        onItemSelected(newValue != null ? newValue.getValue() : null);
    }

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

    private void displayProjectDetails(ProjectDTO projectSummary) {
        projectNameLabel.setText(projectSummary.getName());
        projectDescriptionTextArea.setText(projectSummary.getDescription());
    }

    private void clearProjectDetails() {
        projectNameLabel.setText("");
        projectDescriptionTextArea.setText("");
    }
}