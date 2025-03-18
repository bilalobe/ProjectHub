package com.projecthub.base.project.infrastructure.service;

import com.projecthub.base.project.api.dto.ProjectStatistics;
import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.project.infrastructure.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectStatisticsService {

    private final ProjectRepository projectRepository;

    @Cacheable(value = "project-statistics", key = "#teamId?:'all'", unless = "#result == null")
    @Transactional(readOnly = true)
    public ProjectStatistics getProjectStatistics(UUID teamId) {
        List<Project> projects;
        if (teamId != null) {
            projects = projectRepository.findByTeamId(teamId);
        } else {
            projects = projectRepository.findAll();
        }

        Map<ProjectStatus, Long> statusCounts = projects.stream()
            .collect(Collectors.groupingBy(Project::getStatus, Collectors.counting()));

        long totalProjects = projects.size();
        long completedOnTime = projects.stream()
            .filter(p -> p.getStatus() == ProjectStatus.COMPLETED)
            .filter(p -> !isOverdue(p))
            .count();

        double completionRate = totalProjects > 0 
            ? (double) completedOnTime / totalProjects * 100 
            : 0.0;

        double averageDuration = projects.stream()
            .filter(p -> p.getStartDate() != null && p.getEndDate() != null)
            .mapToLong(p -> ChronoUnit.DAYS.between(p.getStartDate(), p.getEndDate()))
            .average()
            .orElse(0.0);

        return new ProjectStatistics(
            totalProjects,
            statusCounts.getOrDefault(ProjectStatus.ACTIVE, 0L),
            statusCounts.getOrDefault(ProjectStatus.COMPLETED, 0L),
            statusCounts.getOrDefault(ProjectStatus.OVERDUE, 0L),
            completionRate,
            averageDuration,
            getDelayedProjectsCount(projects),
            calculateRiskMetrics(projects)
        );
    }

    private long getDelayedProjectsCount(List<Project> projects) {
        return projects.stream()
            .filter(this::isOverdue)
            .count();
    }

    private boolean isOverdue(Project project) {
        if (project.getDeadline() == null) return false;
        return LocalDate.now().isAfter(project.getDeadline());
    }

    private Map<String, Double> calculateRiskMetrics(List<Project> projects) {
        long highRiskCount = projects.stream()
            .filter(p -> isOverdue(p) || isNearDeadline(p))
            .count();

        double riskPercentage = projects.isEmpty() ? 0.0 
            : (double) highRiskCount / projects.size() * 100;

        return Map.of(
            "highRiskPercentage", riskPercentage,
            "projectHealthIndex", calculateProjectHealthIndex(projects)
        );
    }

    private boolean isNearDeadline(Project project) {
        if (project.getDeadline() == null) return false;
        LocalDate warningDate = LocalDate.now().plusDays(7);
        return project.getDeadline().isBefore(warningDate);
    }

    private double calculateProjectHealthIndex(List<Project> projects) {
        if (projects.isEmpty()) return 100.0;
        
        long healthyProjects = projects.stream()
            .filter(p -> !isOverdue(p) && !isNearDeadline(p))
            .count();
            
        return (double) healthyProjects / projects.size() * 100;
    }
}