package com.projecthub.base.project.infrastructure.service;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.project.infrastructure.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectSchedulingService {
    
    private final ProjectRepository projectRepository;
    private final ProjectNotificationService notificationService;

    @Scheduled(cron = "${projecthub.project.schedule.overdue-check:0 0 0 * * ?}")
    @Transactional
    public void checkForOverdueProjects() {
        List<Project> overdueProjects = projectRepository.findOverdueProjects();
        
        for (Project project : overdueProjects) {
            log.info("Project {} is overdue", project.getId());
            project.setStatus(ProjectStatus.OVERDUE);
            projectRepository.save(project);
            notificationService.sendOverdueNotification(project);
        }
    }

    @Scheduled(cron = "${projecthub.project.schedule.deadline-reminder:0 0 9 * * ?}")
    public void sendDeadlineReminders() {
        LocalDate upcomingDeadline = LocalDate.now().plusDays(7);
        List<Project> projectsNearingDeadline = projectRepository.findByDeadlineBefore(upcomingDeadline);
        
        for (Project project : projectsNearingDeadline) {
            notificationService.sendDeadlineReminderNotification(project);
        }
    }

    @Scheduled(cron = "${projecthub.project.schedule.auto-archive:0 0 1 * * ?}")
    @Transactional
    public void archiveCompletedProjects() {
        List<Project> completedProjects = projectRepository.findByStatus(ProjectStatus.COMPLETED);
        LocalDate archiveThreshold = LocalDate.now().minusMonths(3);
        
        for (Project project : completedProjects) {
            if (project.getEndDate() != null && project.getEndDate().isBefore(archiveThreshold)) {
                project.setStatus(ProjectStatus.ARCHIVED);
                projectRepository.save(project);
                notificationService.sendProjectArchivedNotification(project);
            }
        }
    }
}