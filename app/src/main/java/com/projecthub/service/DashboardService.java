package com.projecthub.service;

import com.projecthub.repository.DashboardRepository;
import com.projecthub.dto.ProjectStatusDTO;
import com.projecthub.dto.RecentActivityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for dashboard metrics and analytics.
 */
@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    @Autowired
    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    /**
     * Gets the total number of users.
     *
     * @return the total user count
     */
    public int getTotalUsers() {
        return dashboardRepository.countTotalUsers();
    }

    /**
     * Gets the total number of projects.
     *
     * @return the total project count
     */
    public int getTotalProjects() {
        return dashboardRepository.countTotalProjects();
    }

    /**
     * Gets the total number of teams.
     *
     * @return the total team count
     */
    public int getTotalTeams() {
        return dashboardRepository.countTotalTeams();
    }

    /**
     * Gets the distribution of project statuses.
     *
     * @return a list of project status DTOs
     */
    public List<ProjectStatusDTO> getProjectStatusDistribution() {
        return dashboardRepository.getProjectStatusDistribution();
    }

    /**
     * Gets the recent activities.
     *
     * @return a list of recent activity DTOs
     */
    public List<RecentActivityDTO> getRecentActivities() {
        return dashboardRepository.getRecentActivities();
    }
}