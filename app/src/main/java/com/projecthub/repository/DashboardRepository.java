package com.projecthub.repository;

import com.projecthub.dto.ProjectStatusDTO;
import com.projecthub.dto.RecentActivityDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DashboardRepository {

    public int countTotalUsers() {
        // Query to count total users
        return 100; // Example value
    }

    public int countTotalProjects() {
        // Query to count total projects
        return 50; // Example value
    }

    public int countTotalTeams() {
        // Query to count total teams
        return 10; // Example value
    }

    public List<ProjectStatusDTO> getProjectStatusDistribution() {
        // Query to get project status distribution
        return List.of(
            new ProjectStatusDTO("Completed", 30),
            new ProjectStatusDTO("In Progress", 15),
            new ProjectStatusDTO("Not Started", 5)
        );
    }

    public List<RecentActivityDTO> getRecentActivities() {
        // Query to get recent activities
        return List.of(
            new RecentActivityDTO("2023-10-01 10:00", "Created Project", "User1"),
            new RecentActivityDTO("2023-10-02 11:00", "Updated Team", "User2")
        );
    }
}