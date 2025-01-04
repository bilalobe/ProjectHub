package com.projecthub.ui.modules.dashboard;

import com.projecthub.ui.shared.utils.BaseController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

@Component
public class DashboardController extends BaseController {


    private DashboardViewModel dashboardViewModel;

    @FXML
    private VBox statisticsPane;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label totalProjectsLabel;

    @FXML
    private Label totalTeamsLabel;

    @FXML
    private PieChart statusPieChart;

    @FXML
    private TableView<DashboardViewModel.RecentActivity> recentActivitiesTable;

    @FXML
    private TableColumn<DashboardViewModel.RecentActivity, String> timestampColumn;

    @FXML
    private TableColumn<DashboardViewModel.RecentActivity, String> activityColumn;

    @FXML
    private TableColumn<DashboardViewModel.RecentActivity, String> userColumn;

    @FXML
    public void initialize() {
        bindProperties();
        setupRecentActivitiesTable();
        dashboardViewModel.loadDashboardData();
    }

    private void bindProperties() {
        totalUsersLabel.textProperty().bind(dashboardViewModel.totalUsersProperty().asString());
        totalProjectsLabel.textProperty().bind(dashboardViewModel.totalProjectsProperty().asString());
        totalTeamsLabel.textProperty().bind(dashboardViewModel.totalTeamsProperty().asString());
        statusPieChart.dataProperty().bind(new SimpleObjectProperty<>(dashboardViewModel.getProjectStatusDistribution()));
    }

    private void setupRecentActivitiesTable() {
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        activityColumn.setCellValueFactory(new PropertyValueFactory<>("activity"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        recentActivitiesTable.setItems(dashboardViewModel.getRecentActivities());
    }
}
