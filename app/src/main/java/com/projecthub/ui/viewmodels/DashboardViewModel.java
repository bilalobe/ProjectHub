package com.projecthub.ui.viewmodels;

import com.projecthub.core.services.dashboard.DashboardService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DashboardViewModel {

    private final DashboardService dashboardService;

    private final IntegerProperty totalUsers = new SimpleIntegerProperty();
    private final IntegerProperty totalProjects = new SimpleIntegerProperty();
    private final IntegerProperty totalTeams = new SimpleIntegerProperty();
    private final ObservableList<PieChart.Data> projectStatusDistribution = FXCollections.observableArrayList();
    private final ObservableList<RecentActivity> recentActivities = FXCollections.observableArrayList();


    public DashboardViewModel(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    public IntegerProperty totalUsersProperty() {
        return totalUsers;
    }

    public IntegerProperty totalProjectsProperty() {
        return totalProjects;
    }

    public IntegerProperty totalTeamsProperty() {
        return totalTeams;
    }

    public ObservableList<PieChart.Data> getProjectStatusDistribution() {
        return projectStatusDistribution;
    }

    public ObservableList<RecentActivity> getRecentActivities() {
        return recentActivities;
    }

    public void loadDashboardData() {
        totalUsers.set(dashboardService.getTotalUsers());
        totalProjects.set(dashboardService.getTotalProjects());
        totalTeams.set(dashboardService.getTotalTeams());

        projectStatusDistribution.setAll(
                dashboardService.getProjectStatusDistribution().stream()
                        .map(dto -> new PieChart.Data(dto.getStatus(), dto.getCount()))
                        .collect(Collectors.toList())
        );

        recentActivities.setAll(
                dashboardService.getRecentActivities().stream()
                        .map(dto -> new RecentActivity(dto.getTimestamp(), dto.getActivity(), dto.getUser()))
                        .collect(Collectors.toList())
        );
    }

    public static class RecentActivity {
        private final String timestamp;
        private final String activity;
        private final String user;

        public RecentActivity(String timestamp, String activity, String user) {
            this.timestamp = timestamp;
            this.activity = activity;
            this.user = user;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getActivity() {
            return activity;
        }

        public String getUser() {
            return user;
        }
    }
}