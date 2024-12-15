package com.projecthub.core.repositories.jpa;

import com.projecthub.core.dto.ProjectStatusDTO;
import com.projecthub.core.dto.RecentActivityDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardJpaRepository {

    int countTotalUsers();

    int countTotalProjects();

    int countTotalTeams();

    List<ProjectStatusDTO> getProjectStatusDistribution();

    List<RecentActivityDTO> getRecentActivities();
}