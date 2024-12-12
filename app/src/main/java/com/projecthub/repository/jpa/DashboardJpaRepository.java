package com.projecthub.repository.jpa;

import com.projecthub.dto.ProjectStatusDTO;
import com.projecthub.dto.RecentActivityDTO;

import java.util.List;

public interface DashboardJpaRepository {
    int countTotalUsers();
    int countTotalProjects();
    int countTotalTeams();
    List<ProjectStatusDTO> getProjectStatusDistribution();
    List<RecentActivityDTO> getRecentActivities();
}