package com.projecthub.ui.modules.submission;

import com.projecthub.ui.modules.submission.viewmodel.SubmissionListViewModel;
import com.projecthub.ui.navigation.NavigationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

@Component
public class SubmissionListController {

    private final SubmissionListViewModel viewModel;
    private final NavigationService navigationService;

    @FXML
    private TableView<SubmissionDTO> submissionsTable;
    @FXML
    private ComboBox<SubmissionStatus> statusFilter;
    @FXML
    private ComboBox<ProjectDTO> projectFilter;
    @FXML
    private TextField searchField;
    @FXML
    private Pagination submissionPagination;

    public SubmissionListController(SubmissionListViewModel viewModel, NavigationService navigationService) {
        this.viewModel = viewModel;
        this.navigationService = navigationService;
    }

    @FXML
    public void initialize() {
        setupTable();
        setupFilters();
        setupPagination();
        loadData();
    }

    private void setupTable() {
        // Setup table columns and bindings
        submissionsTable.setItems(viewModel.getSubmissions());
        submissionsTable.setOnMouseClicked(this::handleTableClick);
    }

    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList(SubmissionStatus.values()));
        statusFilter.valueProperty().addListener((obs, old, newVal) -> viewModel.filterByStatus(newVal));

        projectFilter.setItems(viewModel.getProjects());
        projectFilter.valueProperty().addListener((obs, old, newVal) -> viewModel.filterByProject(newVal));

        searchField.textProperty().addListener((obs, old, newVal) -> viewModel.filterBySearchTerm(newVal));
    }

    private void setupPagination() {
        submissionPagination.pageCountProperty().bind(viewModel.pageCountProperty());
        submissionPagination.currentPageIndexProperty().addListener((obs, old, newVal) -> 
            viewModel.loadPage(newVal.intValue()));
    }

    private void loadData() {
        viewModel.loadSubmissions();
    }

    @FXML
    private void handleNewSubmission() {
        navigationService.navigateToSubmissionDetails(null);
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    private void handleTableClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            SubmissionDTO selected = submissionsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                navigationService.navigateToSubmissionDetails(selected.getId());
            }
        }
    }
}
