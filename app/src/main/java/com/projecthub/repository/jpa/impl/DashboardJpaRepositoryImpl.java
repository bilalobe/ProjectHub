package com.projecthub.repository.jpa.impl;

import com.projecthub.dto.ProjectStatusDTO;
import com.projecthub.dto.RecentActivityDTO;
import com.projecthub.repository.jpa.DashboardJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DashboardJpaRepositoryImpl implements DashboardJpaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public int countTotalUsers() {
        String query = "SELECT COUNT(u) FROM AppUser u";
        return ((Number) entityManager.createQuery(query).getSingleResult()).intValue();
    }

    @Override
    public int countTotalProjects() {
        String query = "SELECT COUNT(p) FROM Project p";
        return ((Number) entityManager.createQuery(query).getSingleResult()).intValue();
    }

    @Override
    public int countTotalTeams() {
        String query = "SELECT COUNT(t) FROM Team t";
        return ((Number) entityManager.createQuery(query).getSingleResult()).intValue();
    }

    @Override
    public List<ProjectStatusDTO> getProjectStatusDistribution() {
        String query = "SELECT new com.projecthub.dto.ProjectStatusDTO(p.status, COUNT(p)) FROM Project p GROUP BY p.status";
        return entityManager.createQuery(query, ProjectStatusDTO.class).getResultList();
    }

    @Override
    public List<RecentActivityDTO> getRecentActivities() {
        String query = "SELECT new com.projecthub.dto.RecentActivityDTO(a.timestamp, a.activity, a.user.username) FROM Activity a ORDER BY a.timestamp DESC";
        return entityManager.createQuery(query, RecentActivityDTO.class).setMaxResults(10).getResultList();
    }
}